* AWSS3LookupApplication performs the following functions:
* Download the zip file from the s3 bucket[AWSS3DownloadService], and extract csv files from it[ExtractZipFileService].
* Read the CSV, search and extract lines which contain the word "ellipsis" (in any field)[FileOperationService]
* Include any lines with a match and convert into Apache Parquet format[FileOperationService]
* Name the file with the same name as the csv (e.g. matching lines in news.csv → news.parquet)[FileOperationService], and
* upload the files in the same S3 bucket[AWSS3UploadService].
* Prerequisites for running the application:
* Java 8 is installed on the machine.
* Download the code from GitHub: git@github.com:manojsharda/awss3lookup.git
* Update application.properties(under resources folder) file with your AWS S3 bucket details:
* cloud.aws.credentials.accessKey
* cloud.aws.credentials.secretKey
* cloud.aws.region.static
* application.bucket.name
* build the code using gradle or if you are using IDE like IntelliJ run the application by running the AWSs3lookupApplication which is the Main class after building the project.
* References links:
* https://www.techgeeknext.com/cloud/aws/amazon-s3-springboot-download-file-in-s3-bucket
* https://parquet.apache.org/documentation/latest/
* https://arrow.apache.org/docs/java/index.html
* https://blog.contactsunny.com/data-science/how-to-generate-parquet-files-in-java
* https://stackoverflow.com/questions/39728854/create-parquet-files-in-java
