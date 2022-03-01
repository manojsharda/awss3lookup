* AWSS3LookupApplication performs the following functions:
* Download the zip file from the s3 bucket[AWSS3DownloadService], and extract csv files from it[ExtractZipFileService].
* Read the CSV, search and extract lines which contain the word "ellipsis" (in any field)[FileOperationService]
* Include any lines with a match and convert into Apache Parquet format[FileOperationService]
* Name the file with the same name as the csv (e.g. matching lines in news.csv → news.parquet)[FileOperationService], and
* upload the files in the same S3 bucket[AWSS3UploadService].

* References links:
* https://www.techgeeknext.com/cloud/aws/amazon-s3-springboot-download-file-in-s3-bucket
* https://parquet.apache.org/documentation/latest/
* https://arrow.apache.org/docs/java/index.html
* https://blog.contactsunny.com/data-science/how-to-generate-parquet-files-in-java
* https://stackoverflow.com/questions/39728854/create-parquet-files-in-java
