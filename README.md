# ProtoSchemaGenerator
This repository contains a tool called ProtoGen, which is designed to automate the process of converting Java classes with dependencies into .proto files or protobuf schemas. With ProtoGen, you can generate the necessary protobuf schemas without having to manually write them for every Java bean.

# Installation
To use ProtoSchemaGen, follow these steps:

Clone this repository to your local machine:

`git clone https://github.com/your-username/ProtoSchemaGenerator.git`

Install the required dependencies.

# Usage

You can define your own set of classes and nested dependencies in the root class.
Put root class name instead of Person.class in the main file and give name of directory where you want to generate .proto files for your root class and its dependencies.

Run the main file and you can see directory folder will be made with all the required proto files with necessary imports.
