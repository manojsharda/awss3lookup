package com.example.awss3lookup.service;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class FileOperationService {
    private Logger logger = LoggerFactory.getLogger(FileOperationService.class);
    @Autowired
    private AWSS3UploadService awsS3UploadService;
    @Value("${application.csv.file.lookupString}")
    private String lookupString;
    @Value("${application.csv.file.path}")
    private String csvFilesDir;
    @Value("${application.parquet.file.path}")
    private String parquetFilesDir;
    @Value("${application.parquet.file.ext}")
    private String parquetFileExt;
    @Value("${application.parquet.schema.file.ext}")
    private String parquetSchemaFileExt;

    public void searchExtractedFiles() {
        File[] files = new File(csvFilesDir).listFiles(obj -> obj.isFile() && obj.getName().endsWith(".csv"));
        Arrays.stream(files).forEach(file -> {
            List<String> matchingRecords = searchCsvFileForSearchString(file.getName());
            if (!matchingRecords.isEmpty()) {
                List<GenericData.Record> genericDataRecords = new ArrayList<>();
                String fileNameWithOutExt = FilenameUtils.removeExtension(file.getName());
                Schema schema = createFileSchema(fileNameWithOutExt);
                matchingRecords.forEach(s -> genericDataRecords.add(createGenericRecord(s, schema)));
                org.apache.hadoop.fs.Path path =
                        new org.apache.hadoop.fs.Path(parquetFilesDir + fileNameWithOutExt + parquetFileExt);
                writeGenericRecordsToFile(genericDataRecords, path, schema, file.getName());
            }
        });
    }

    private List<String> searchCsvFileForSearchString(String fileName) {
        String file = csvFilesDir + fileName;
        List<String> matchingRecords = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().contains(lookupString)) {
                    matchingRecords.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Search Failed for file:" + fileName);
            return new ArrayList<>();
        }
        return matchingRecords;
    }

    private Schema createFileSchema(String fileName) {
        Schema schema;
        String schemaLocation = "/" + fileName + parquetSchemaFileExt;
        try (InputStream inStream = FileOperationService.class.getResourceAsStream(schemaLocation)) {
            schema = new Schema.Parser().parse(IOUtils.toString(inStream, "UTF-8"));
        } catch (Exception e) {
            logger.error("Error accessing SCHEMA file from :" + schemaLocation);
            throw new RuntimeException("Error accessing SCHEMA file from :" + schemaLocation, e);
        }
        return schema;
    }

    private GenericData.Record createGenericRecord(String record, Schema schema) {
        GenericData.Record genericDataRecord = new GenericData.Record(schema);
        AtomicInteger count = new AtomicInteger(1);
        String[] recordSplit = record.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        Arrays.asList(recordSplit).stream().forEach(s -> genericDataRecord.put("col" + count.getAndIncrement(), s));
        return genericDataRecord;
    }

    private void writeGenericRecordsToFile(List<GenericData.Record> genericRecords, org.apache.hadoop.fs.Path fileToWrite, Schema schema, String fileName) {
        try (ParquetWriter<GenericData.Record> writer = AvroParquetWriter
                .<GenericData.Record>builder(fileToWrite)
                .withSchema(schema)
                .withConf(new Configuration())
                .withCompressionCodec(CompressionCodecName.SNAPPY)
                .build()) {

            for (GenericData.Record record : genericRecords) {
                writer.write(record);
            }
        } catch (IOException exception) {
            logger.error("Writing to file:" + fileName + "Failed.");
        } finally {
            awsS3UploadService.uploadFileToS3(FilenameUtils.removeExtension(fileName) + ".parquet");
        }
    }
}
