# Vaccines-COVID19
A distributed systems approach for emulate vaccines distribution for COVID-19. Written in Java and Rust. Using RMI, MongoDB and Ansible.

## Installation
Each project has its own Maven Archetype for build into a distributable jar. Execute:
```bash
mvn compile build
```
to build each jar contained in the src folder.

For the laboratory, please install Rust Cargo and compile with release flag.

## Usage
Include each jar in the CLASSPATH enviromental variable. Then run in this order:
1. Run an instance of MongoDB (Docker based or barebones).
2. Execute laboratory binary compiled previously.
3. Execute the RMIRegistry in each machine (min. 2) and run the Distribution Center RMI.
4. Execute the Load Balancer in another VM or Docker Container.
5. Connect to the system using the EPSClient frontend.

## Contributing
This repository is in Read Mode. No further changes are expected to be included.

## Authors
Mateo Florido

## License
[MIT](https://choosealicense.com/licenses/mit/)
