# Bloom Services

[![Production](https://github.com/swardeo/bloom-services/actions/workflows/production.yaml/badge.svg?branch=master)](https://github.com/swardeo/bloom-services/actions/workflows/production.yaml)

[![Development](https://github.com/swardeo/bloom-services/actions/workflows/development.yaml/badge.svg)](https://github.com/swardeo/bloom-services/actions/workflows/development.yaml)

## What is Bloom?

Bloom was my final year project at Aston University, which aimed to help individuals explore a forecast of their financial situation.

This project contains the backend of the application. The frontend can be viewed [here](https://github.com/swardeo/bloom-ui).

### Bloom is now offline :(

**PLEASE NOTE: the bloom.money domain is no longer associated with myself or this project.**

## API Definition

- The API definition for Bloom can be found [here](api-definition.yaml), where it has been modelled using the OpenAPI 3.0 specification.

## Commands

In the project directory, you can run:

- `./gradlew clean test` to perform a clean run of the unit tests.
- `./gradlew clean buildZip` to perform a clean build of the application and generate the artifacts needed for deployment (output to the `build/artifacts` directory).

## Deployment Environments

### Development

A pipeline will deploy the application to the development environment automatically when a pull request is raised against the master branch.

The development API can be reached at ~~removed~~.

### Production

Another pipeline will deploy the application to the production environment automatically when a pull request has been merged to the master branch.

The production API can be reached at ~~removed~~.

### Deployment Validation

To be able to validate each deployment, I have created Integration Tests, locally using Postman, which can run against either environment.

I have also created a Node.js function which allows me to retrieve the `Authorization` token required by most endpoints, this can then be supplied to the Integration Tests.

It would have been preferable to have automated these as a part of both pipelines, but this approach seemed more convenient given the time constraints faced.

## Infrastructure

The application is deployed to Amazon Web Services.

The infrastructure is modelled using AWS CloudFormation templates, which can be viewed [here](.cloudformation/stack.yaml).

## Scope

As this forms part of a time-constrained academic project, the scope has been limited, some details have been knowingly overlooked, and some things would have been done differently if time had allowed for it.

## Notes about the project

- No dependency injection framework has been used, given that these can be expensive in resource constrained environments such as Lambda.
  - As a result, most important dependencies are initialised manually and passed through to constructors. Some less important ones have been created inline, though the same process could be applied to these if needed.
- An attempt was made to follow a strict microservices approach with a single function per action.
  - With the API currently only receiving a small amount of traffic, users could fall victim to each individual function cold starting on them (which is not a good experience). Given the number of functions, pre-warming each Lambda function did not seem possible within free tier.
  - One approach to avoid this could have been by using a single Lambda function for all operations related to savings and another for those related to debts. These would then have received a higher level of traffic and been easier to keep warm as a result.
- On the subject of cold starts, for Bloom they seem to be worse than expected even for a Java Lambda function, despite some of the efforts made to limit this, so I think there are other contributing factors.
  - For each Lambda function, the first invocation of each function almost always takes 5+ seconds. Following this, execution times return to a reasonable level.
  - This is also observed locally when an object containing `LoggerFactory.getLogger(...)` is constructed for the first time. Any further constructions of the same object take a tiny amount of time (as is expected).
  - Unfortunately, I have not had enough time to investigate this fully, though there does seem to be some connection.
