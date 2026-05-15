# EURIS TEST JAVA

## Tecnologie utilizzate
- **Java**: 25
- **Spring Boot**: 4.0.6
- **Database**: PostgreSQL (Container Docker)
- **Mapper**: MapStruct
- **Lombok**
- **Test**: JUnit 5 + Testcontainers

## Come avviare il progetto
### Avviare il database PostgreSQL con docker-compose; il file docker-compose.yml è nella cartella README

```bash
docker-compose up -d
```

### Avviare l'applicazione Spring 

```bash
./mvnw spring-boot:run
```

## Postman Collection

Nella cartella `README` è disponibile una collection con tutte le chiamate API di test `euris.postman_collection.json`.
Importare il file `euris.postman_collection.json` in Postman per avere a disposizione gli endpoint con delle chiamate di esempio.