# Diluv-API

# Contribute

## How to run
Requirements:
- Java 8
- Download and install MariaDB (Recommend using [Windows Subsystem for Linux](https://docs.microsoft.com/en-us/windows/wsl/install-win10))
- Generate private/public keys (Shown below)

### Generating public/private key
**In working directory**
```
openssl genrsa 2048 > rsa.private
```
#### Private Key
```
openssl pkcs8 -topk8 -inform PEM -outform PEM -in rsa.private -nocrypt > private.pem
```
#### Public Key
```
openssl rsa -inform PEM -in rsa.private -pubout > public.pem
```