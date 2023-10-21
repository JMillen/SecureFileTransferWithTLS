# Secure File Transfer with TLS

This project implements a secure file transfer system in Java using Transport Layer Security (TLS). The system consists of a server and a client that enable secure file transfer between them.

## Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Usage](#usage)
- [Project Structure](#project-structure)

## Project Overview

This project serves as an introduction to network application programming in Java with a focus on security and was done in a COMPX204 paper. It covers the following key components:

- Implementing the TLS handshake for secure communication.
- Secure file transfer between the server and client.

## Features

- Secure TLS communication between the server and client.
- Client-server architecture for file transfer.
- Server authentication with certificates.
- File request and transfer functionalities.

## Getting Started

### Prerequisites

Before running the project, ensure you have the following installed:

- Java Development Kit (JDK)
- OpenSSL (for certificate generation)

### Usage

1. Clone this repository:

2. Compile and run the server:
   
  - javac MyTLSFileServer.java
  - java MyTLSFileServer <port>

3. Compile and run the Client:

 - javac MyTLSFileClient.java
 - java MyTLSFileClient <hostname> <port> <filename>


## Project Structure:

![image](https://github.com/JMillen/SecureFileTransferWithTLS/assets/66464271/bb59324d-916e-4c72-a2c6-547da0a203b9)


