# Biometric authentication
Diploma thesis at the end of my engineering studies. The project aimed to provide a universal biometric login solution to the applications. The whole system consists of several cooperating elements. Communication between them happen using a previously generated token. The token is transferred between parts of the system that change and track its state.

<img src="https://github.com/kpmacion/Biometric_authentication/blob/master/images/system_diagram.png" width="500"/>


# Physical module
The device consists of the Arduino UNO board in a version with a built-in WiFi module, e-paper display, optical fingerprint scanner, and batteries. The main task of the device is to scan new fingerprints, add them to the system, and then compare them with other scans. The whole thing was packed in a case printed on a 3D printer.

<img src="https://github.com/kpmacion/Biometric_authentication/blob/master/images/physical_module_elements.jpg" width="400"/> <img src="https://github.com/kpmacion/Biometric_authentication/blob/master/images/physical_module_case.jpg" width="400"/>


# Authentication API
REST API, whose role is to mediate in communication between system elements. It provides the endpoints needed to control the entire system. It was implemented in Java using the Spring Boot framework and the Hibernate object-relational mapper to easily map Java objects to database records.

<img src="https://github.com/kpmacion/Biometric_authentication/blob/master/images/api_response.png" width="500"/>


# Database
A database that stores information about generated tokens, applications supported by the system and their users. It is a relational database in the PostgreSQL implementation. It was launched from the Docker image.

<img src="https://github.com/kpmacion/Biometric_authentication/blob/master/images/db_diagram.png" width="500"/>


# System presentation
https://user-images.githubusercontent.com/80364596/224540222-1dbfa36b-25eb-42e3-9f41-8821da7fb4e1.mp4

