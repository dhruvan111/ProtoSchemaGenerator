# ProtoSchemaGenerator
This repository contains a tool called ProtoGen, which is designed to automate the process of converting Java classes with dependencies into .proto files or protobuf schemas. With ProtoGen, you can generate the necessary protobuf schemas without having to manually write them for every Java bean.

Installation
To use ProtoSchemaGen, follow these steps:

Clone this repository to your local machine:

`git clone https://github.com/your-username/ProtoGen.git`

Install the required dependencies.

Build the ProtoGen tool by running the following command in the repository's root directory:

`./gradlew build`

Usage
Once you have ProtoGen installed, you can use it to generate protobuf schemas for your Java classes. Here's how you can do it:

1. Place your Java classes in the src/main/java directory.

2. Run the ProtoGen tool with the following command:
`java -jar protogen.jar`
This command will analyze the Java classes in the src/main/java directory and automatically generate the corresponding .proto files or protobuf schemas.

3. The generated .proto files will be saved in the output/proto directory. You can customize the output directory by modifying the configuration in the config.properties file.

