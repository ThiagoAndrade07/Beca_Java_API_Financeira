# Beca Java 2026 — API Financeira

Projeto desenvolvido para o desafio Beca Java 2026.

O repositório contém o MS1 — User Service, responsável pelo cadastro, consulta, atualização, exclusão, autenticação e importação de usuários por CSV.

## Tecnologias

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Bean Validation
- PostgreSQL 17
- BCrypt
- Maven
- JUnit 5
- Mockito
- Swagger/OpenAPI
- Docker e Docker Compose

## Funcionalidades

- Cadastro de usuários
- Listagem de usuários
- Consulta de usuário por ID
- Atualização de nome e e-mail
- Exclusão de usuários
- Autenticação HTTP Basic
- Proteção de senhas com BCrypt
- Importação parcial de usuários por CSV
- Tratamento padronizado de erros HTTP
- Documentação interativa com Swagger
- Testes automatizados

## Arquitetura

O User Service utiliza uma arquitetura hexagonal simples.

```text
userservice
├── domain
│   ├── model
│   └── exception
├── application
│   ├── model
│   ├── service
│   ├── exception
│   └── validation
├── port
│   ├── in
│   └── out
└── adapter
    ├── in
    │   ├── web
    │   ├── csv
    │   └── security
    └── out
        ├── persistence
        └── security
```

### Domain

Contém o modelo de usuário e as exceções relacionadas às regras de negócio.

### Application

Contém os serviços que implementam os casos de uso, como cadastrar, listar, atualizar, excluir e importar usuários.

### Port

Contém as interfaces que definem como o núcleo da aplicação se comunica com as entradas e saídas.

- `port.in`: operações oferecidas pela aplicação.
- `port.out`: recursos externos utilizados pela aplicação.

### Adapter

Conecta a aplicação às tecnologias externas.

- `adapter.in.web`: controllers e DTOs HTTP.
- `adapter.in.csv`: leitura de arquivos CSV.
- `adapter.in.security`: autenticação HTTP Basic.
- `adapter.out.persistence`: integração com JPA e PostgreSQL.
- `adapter.out.security`: implementação do BCrypt.

O fluxo principal é:

```text
Requisição HTTP
→ Controller
→ Port de entrada
→ Serviço de aplicação
→ Port de saída
→ Adapter JPA
→ PostgreSQL
```

## Pré-requisitos

Para executar com Docker:

- Git
- Docker Desktop
- Docker Compose

Para executar sem o container da aplicação:

- Java 21
- PostgreSQL ou container PostgreSQL

## Configuração do ambiente

Clone o repositório:

```powershell
git clone https://github.com/ThiagoAndrade07/Beca_Java_API_Financeira.git
cd Beca_Java_API_Financeira
```

Crie o arquivo `.env` a partir do exemplo:

```powershell
Copy-Item .env.example .env
```

Edite o `.env` e defina uma senha local:

```env
POSTGRES_DB=beca_financeira
POSTGRES_USER=beca_user
POSTGRES_PASSWORD=defina_uma_senha_local
```

O arquivo `.env` não é enviado ao GitHub.

## Executando com Docker Compose

Suba o PostgreSQL e o User Service:

```powershell
docker compose up -d --build
```

Confira os containers:

```powershell
docker compose ps
```

A API estará disponível em:

```text
http://localhost:8081
```

O PostgreSQL estará acessível pelo computador em:

```text
localhost:5434
```

Para acompanhar os logs:

```powershell
docker compose logs -f user-service
```

Para encerrar os containers:

```powershell
docker compose down
```

O volume do PostgreSQL preserva os dados quando os containers são recriados.

## Executando localmente

Suba somente o PostgreSQL:

```powershell
docker compose up -d postgres
```

Execute a aplicação:

```powershell
.\user-service\mvnw.cmd -f .\user-service\pom.xml spring-boot:run
```

A aplicação procura o `.env` na pasta atual e na pasta superior, permitindo a execução pela raiz ou por `user-service`.

## Swagger/OpenAPI

Com a aplicação em execução, acesse:

```text
http://localhost:8081/swagger-ui.html
```

A especificação OpenAPI em JSON está disponível em:

```text
http://localhost:8081/v3/api-docs
```

O botão `Authorize` do Swagger permite informar o e-mail e a senha de um usuário cadastrado para testar os endpoints protegidos.

## Autenticação

O projeto utiliza HTTP Basic.

As credenciais são:

- Username: e-mail do usuário
- Password: senha original cadastrada

A senha não é armazenada diretamente. O sistema salva somente o hash gerado pelo BCrypt.

O endpoint de cadastro é público:

```text
POST /users
```

Os demais endpoints exigem autenticação.

## Endpoints

| Método | Endpoint | Descrição | Autenticação |
|---|---|---|---|
| POST | `/users` | Cadastrar usuário | Não |
| GET | `/users` | Listar usuários | Sim |
| GET | `/users/{id}` | Consultar usuário por ID | Sim |
| PUT | `/users/{id}` | Atualizar usuário | Sim |
| DELETE | `/users/{id}` | Excluir usuário | Sim |
| POST | `/users/import` | Importar usuários por CSV | Sim |

Nenhuma resposta HTTP retorna a senha ou o hash da senha.

## Importação CSV

O arquivo deve utilizar o cabeçalho:

```csv
name,email,password
```

Exemplo:

```csv
name,email,password
Maria Silva,maria@example.com,Senha123
João Souza,joao@example.com,OutraSenha456
```

Um arquivo de exemplo está disponível em:

```text
user-service/examples/users-import-example.csv
```

Regras da importação:

- máximo de 1.000 usuários por arquivo;
- as linhas válidas são cadastradas;
- linhas inválidas não interrompem o restante do arquivo;
- e-mails duplicados são informados como erro da linha;
- o relatório apresenta quantos usuários foram importados e quais linhas falharam;
- arquivos com BOM UTF-8 são aceitos;
- CSV malformado retorna erro HTTP 400.

## Validação de senha

A senha deve:

- possuir entre 8 e 72 caracteres;
- ocupar no máximo 72 bytes em UTF-8.

A validação por bytes evita erros do BCrypt com caracteres que ocupam mais de um byte, como letras acentuadas.

## Respostas de erro

A API utiliza um formato padronizado:

```json
{
  "status": 400,
  "message": "Existem campos inválidos",
  "erros": {
    "email": "O e-mail deve ter um formato válido"
  }
}
```

Principais códigos:

- `400`: requisição ou campos inválidos;
- `401`: autenticação necessária ou credenciais inválidas;
- `404`: recurso não encontrado;
- `405`: método HTTP não permitido;
- `409`: e-mail já cadastrado;
- `413`: arquivo maior que o limite permitido;
- `415`: tipo de conteúdo não suportado;
- `500`: erro interno inesperado.

## Testes

Execute a suíte completa:

```powershell
.\user-service\mvnw.cmd -f .\user-service\pom.xml test
```

Os testes cobrem:

- serviços do CRUD;
- normalização de nome e e-mail;
- preservação da senha recebida;
- BCrypt real;
- validação de senha em bytes UTF-8;
- parser CSV;
- importação parcial;
- duplicidade de e-mail;
- persistência;
- autenticação HTTP Basic;
- respostas 401 em JSON.

## Parando e removendo os dados

Para parar os containers sem remover o volume:

```powershell
docker compose down
```

Para remover também o volume e todos os dados locais do PostgreSQL:

```powershell
docker compose down -v
```

Use o segundo comando somente quando quiser reiniciar completamente o banco.