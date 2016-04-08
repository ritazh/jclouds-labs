jclouds Labs - Azure Compute ARM Provider
============

Build status for azurecomputearm module:
[![Build Status](http://devopsfunjenkins.westus.cloudapp.azure.com:8080/buildStatus/icon?job=jclouds-labs-azurecompute-arm/org.apache.jclouds.labs:azurecomputearm)](http://devopsfunjenkins.westus.cloudapp.azure.com:8080/job/jclouds-labs-azurecompute-arm/org.apache.jclouds.labs$azurecomputearm/)


## Setting Up Test Credentials

### Create a Service Principal

Install and configure Azure CLI following these [steps](http://azure.microsoft.com/en-us/documentation/articles/xplat-cli/).

Using the Azure CLI, run the following commands to create a service principal

```bash
# Set mode to ARM
azure config mode arm

# Enter your Microsoft account credentials when prompted
azure login

# Set current subscription to create a service principal
azure account set <Subscription-id>

# Create an AAD application with your information.
azure ad app create --name <name> --password <password> --home-page <home-page> --identifier-uris <identifier-uris>

# For example: azure ad app create --name "jcloudsarm"  --password abcd --home-page "https://jcloudsarm" --identifier-uris "https://jcloudsarm"

# Output will include a value for `Application Id`, which will be used for the live tests

# Create a Service Principal
azure ad sp create <Application-id>

# Output will include a value for `Object Id`

```

Run the following commands to assign roles to the service principal

```bash
# Assign roles for this service principal
azure role assignment create --objectId <Object-id> -o Contributor -c /subscriptions/<Subscription-id>/
```

Verify service principal

```bash
azure login -u <Application-id> -p <password> --service-principal --tenant <Tenant-id>
```

## Run Live Tests

Use the following to run the live tests

```bash

mvn clean verify -Plive \
    -Dtest.azurecompute-arm.identity=<Application-id> \
    -Dtest.azurecompute-arm.credential=<password> \
    -Dtest.azurecompute-arm.endpoint=https://management.azure.com/subscriptions/<Subscription-id> \
    -Dtest.oauth.endpoint=https://login.microsoftonline.com/<Tenant-id>/oauth2/token
```