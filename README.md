# Beca Java 2026 — API de Gestão Financeira

Projeto desenvolvido para o desafio Beca Java 2026.

O sistema é composto por dois microsserviços:

- **MS1 — User Service:** cadastro, autenticação e gerenciamento de usuários.
- **MS2 — Transaction Service:** operações financeiras, consultas, saldo, conversão de moedas, análise de despesas e exportação CSV.

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
- Docker
- Docker Compose
- MockAPI
- BrasilAPI

## Microsserviços

### User Service

Responsável por:

- cadastrar usuários;
- consultar usuários;
- atualizar nome e e-mail;
- excluir usuários;
- autenticar com HTTP Basic;
- armazenar senhas com BCrypt;
- importar usuários por arquivo CSV;
- disponibilizar o usuário autenticado para o Transaction Service.

### Transaction Service

Responsável por:

- cadastrar depósitos;
- cadastrar retiradas;
- cadastrar transferências;
- cadastrar compras;
- aprovar ou rejeitar transações;
- validar saldo e limites pela MockAPI;
- atualizar o saldo depois de operações aprovadas;
- consultar transações do usuário autenticado;
- consultar saldo e limites;
- consultar cotações de dólar e euro pela BrasilAPI;
- armazenar a cotação utilizada em cada transação;
- analisar despesas por categoria, dia ou mês;
- exportar relatório de transações em CSV.

## Arquitetura hexagonal

Os dois microsserviços utilizam uma arquitetura hexagonal simples.

```text
service
├── domain
│   ├── model
│   └── exception
├── application
│   ├── model
│   ├── service
│   └── exception
├── port
│   ├── in
│   └── out
└── adapter
    ├── in
    │   ├── web
    │   └── security
    └── out
        ├── persistence
        └── client
```

### Domain

Contém as entidades e regras essenciais do negócio.

No Transaction Service, o domínio representa:

- transação;
- tipo da transação;
- categoria;
- status;
- cotação e conversão de moedas;
- regras de aprovação e rejeição.

O domínio não conhece controllers, HTTP, PostgreSQL ou APIs externas.

### Application

Coordena os casos de uso da aplicação.

Exemplos:

- criar uma transação;
- consultar uma transação;
- listar transações;
- analisar despesas;
- exportar relatório CSV.

### Port

Contém interfaces que definem os contratos da aplicação.

- `port.in`: operações que a aplicação oferece.
- `port.out`: recursos externos necessários pela aplicação.

Exemplos de portas de saída:

- repositório de transações;
- consulta de saldo e limites;
- consulta de usuário autenticado;
- consulta de cotação.

### Adapter

Conecta a aplicação às tecnologias externas.

- `adapter.in.web`: controllers, DTOs e mappers HTTP.
- `adapter.in.security`: autenticação Basic do MS2 integrada ao MS1.
- `adapter.out.persistence`: JPA e PostgreSQL.
- `adapter.out.client`: User Service, MockAPI e BrasilAPI.

Fluxo principal:

```text
Requisição HTTP
→ autenticação Basic
→ controller
→ port de entrada
→ serviço de aplicação
→ domínio
→ port de saída
→ adapter externo
→ resposta HTTP
```

## Pré-requisitos

- Git
- Docker Desktop
- Docker Compose

Para execução sem Docker:

- Java 21
- Maven ou Maven Wrapper
- PostgreSQL 17

## Configuração

Clone o repositório:

```powershell
git clone https://github.com/ThiagoAndrade07/Beca_Java_API_Financeira.git
cd Beca_Java_API_Financeira
```

Crie o `.env`:

```powershell
Copy-Item .env.example .env
```

Exemplo:

```properties
POSTGRES_DB=beca_financeira
POSTGRES_USER=beca_user
POSTGRES_PASSWORD=defina_uma_senha_local

TRANSACTION_POSTGRES_DB=beca_transactions
TRANSACTION_POSTGRES_USER=transaction_user
TRANSACTION_POSTGRES_PASSWORD=defina_uma_senha_local

MOCK_API_BASE_URL=https://seu-projeto.mockapi.io/v1
BRASIL_API_BASE_URL=https://brasilapi.com.br/api
```

O `.env` contém configurações locais e não deve ser enviado ao GitHub.

## Configuração da MockAPI

Crie um recurso chamado:

```text
accounts
```

Cada registro deve possuir:

```json
{
  "id": "1",
  "userId": 1,
  "balance": 5000,
  "dailyLimit": 2000,
  "transactionLimit": 1000
}
```

O `userId` deve ser igual ao identificador do usuário cadastrado no User Service.

Exemplo com dois usuários:

```json
[
  {
    "id": "1",
    "userId": 1,
    "balance": 5000,
    "dailyLimit": 2000,
    "transactionLimit": 1000
  },
  {
    "id": "2",
    "userId": 2,
    "balance": 2000,
    "dailyLimit": 2000,
    "transactionLimit": 1000
  }
]
```

A MockAPI é externa. Executar `docker compose down -v` remove os bancos locais, mas não remove os registros da MockAPI.

## Executando com Docker Compose

Construa e inicie todo o sistema:

```powershell
docker compose up -d --build
```

Confira os containers:

```powershell
docker compose ps
```

Containers esperados:

- `beca-java-postgres`
- `beca-java-user-service`
- `beca-java-transaction-postgres`
- `beca-java-transaction-service`

Acompanhe os logs:

```powershell
docker compose logs -f
```

Logs somente do MS2:

```powershell
docker compose logs -f transaction-service
```

Pare o sistema preservando os dados:

```powershell
docker compose down
```

Remova também os volumes e dados locais:

```powershell
docker compose down -v
```

Use `down -v` somente quando desejar reiniciar os bancos.

## Endereços

### User Service

```text
http://localhost:8081
```

Swagger:

```text
http://localhost:8081/swagger-ui/index.html
```

OpenAPI:

```text
http://localhost:8081/v3/api-docs
```

### Transaction Service

```text
http://localhost:8082
```

Swagger:

```text
http://localhost:8082/swagger-ui/index.html
```

OpenAPI:

```text
http://localhost:8082/v3/api-docs
```

## Autenticação

O sistema utiliza HTTP Basic.

Credenciais:

- Username: e-mail do usuário.
- Password: senha original cadastrada.

O MS2 não valida a senha diretamente. Ele repassa as credenciais para:

```text
GET /users/me
```

O MS1 valida o e-mail e a senha e devolve os dados do usuário autenticado.

No Swagger:

1. clique em `Authorize`;
2. informe o e-mail;
3. informe a senha;
4. confirme a autorização;
5. execute os endpoints protegidos.

## Endpoints do User Service

| Método | Endpoint | Descrição | Autenticação |
|---|---|---|---|
| POST | `/users` | Cadastrar usuário | Não |
| GET | `/users` | Listar usuários | Sim |
| GET | `/users/{id}` | Consultar usuário | Sim |
| GET | `/users/me` | Consultar usuário autenticado | Sim |
| PUT | `/users/{id}` | Atualizar usuário | Sim |
| DELETE | `/users/{id}` | Excluir usuário | Sim |
| POST | `/users/import` | Importar usuários por CSV | Sim |

Nenhuma resposta retorna senha ou hash.

## Endpoints do Transaction Service

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/transactions` | Criar transação |
| GET | `/transactions` | Listar e filtrar transações |
| GET | `/transactions/{id}` | Consultar transação por ID |
| GET | `/accounts/balance` | Consultar saldo e limites |
| GET | `/transactions/analysis/expenses` | Analisar despesas |
| GET | `/transactions/export/csv` | Exportar relatório CSV |

Todos os endpoints do MS2 exigem autenticação.

## Criando transações

Endpoint:

```text
POST /transactions
```

### Depósito

```json
{
  "amount": 500.00,
  "description": "Depósito inicial",
  "category": "INCOME",
  "type": "DEPOSIT",
  "destinationUserId": null
}
```

O depósito aprovado aumenta o saldo.

### Retirada

```json
{
  "amount": 100.00,
  "description": "Retirada em dinheiro",
  "category": "OTHER",
  "type": "WITHDRAWAL",
  "destinationUserId": null
}
```

A retirada valida saldo e limites antes da aprovação.

### Compra

```json
{
  "amount": 150.00,
  "description": "Compra no mercado",
  "category": "FOOD",
  "type": "PURCHASE",
  "destinationUserId": null
}
```

A compra aprovada reduz o saldo.

### Transferência

```json
{
  "amount": 200.00,
  "description": "Transferência",
  "category": "TRANSFER",
  "type": "TRANSFER",
  "destinationUserId": 2
}
```

A transferência:

- debita o remetente;
- credita o destinatário;
- exige conta de destino existente;
- valida saldo e limites.

## Status das transações

Uma transação pode ficar:

- `APPROVED`: operação aprovada.
- `REJECTED`: operação rejeitada com o motivo registrado.

Transações rejeitadas permanecem armazenadas para histórico.

## Correção de transações

As transações não são editadas nem excluídas.

A correção é representada por uma nova transação compensatória, preservando o histórico da operação original.

Exemplo:

- transação original: retirada de R$ 100;
- correção: depósito de R$ 100;
- a descrição da correção pode mencionar o identificador original.

Não existe endpoint específico de estorno nesta versão.

## Saldo e limites

Endpoint:

```text
GET /accounts/balance
```

A resposta contém:

- saldo atual;
- limite diário;
- limite por transação.

O saldo e os limites são fornecidos pela MockAPI.

Caso a MockAPI esteja indisponível, o MS2 devolve `503 Service Unavailable`.

## Conversão de moedas

Ao criar uma transação, o sistema consulta:

- dólar americano (`USD`);
- euro (`EUR`).

A BrasilAPI fornece a cotação de venda.

O sistema armazena junto da transação:

- valor original em reais;
- cotação do dólar;
- valor convertido em dólar;
- data da cotação do dólar;
- cotação do euro;
- valor convertido em euro;
- data da cotação do euro.

Quando a cotação do dia atual ainda não está disponível, o sistema tenta o dia anterior.

Se a BrasilAPI estiver indisponível:

- a transação continua sendo processada;
- a operação financeira não depende da conversão;
- a resposta informa que a conversão está indisponível.

## Consulta de transações

### Listagem

```text
GET /transactions
```

Filtros opcionais:

- `type`
- `startDate`
- `endDate`

Exemplo:

```text
GET /transactions?type=PURCHASE&startDate=2026-11-01&endDate=2026-11-30
```

O usuário recebe somente as próprias transações.

### Consulta por ID

```text
GET /transactions/{id}
```

A consulta confere se a transação pertence ao usuário autenticado.

## Análise de despesas

Endpoint:

```text
GET /transactions/analysis/expenses
```

Agrupamentos aceitos:

- `CATEGORY`
- `DAY`
- `MONTH`

Exemplo:

```text
GET /transactions/analysis/expenses?groupBy=CATEGORY
```

Com período:

```text
GET /transactions/analysis/expenses?groupBy=DAY&startDate=2026-11-01&endDate=2026-11-30
```

Quando o período não é informado, o sistema utiliza o mês atual no fuso:

```text
America/Sao_Paulo
```

São consideradas despesas aprovadas:

- compra;
- retirada;
- transferência enviada.

Depósitos e transações rejeitadas não entram na análise.

Exemplo de resposta:

```json
{
  "summaries": [
    {
      "group": "FOOD",
      "total": 250.00
    },
    {
      "group": "TRANSPORT",
      "total": 100.00
    }
  ],
  "total": 350.00
}
```

## Exportação CSV

Endpoint:

```text
GET /transactions/export/csv
```

Aceita filtros opcionais por tipo e período.

O arquivo:

- utiliza UTF-8 com BOM;
- preserva os acentos;
- utiliza `;` como separador;
- utiliza vírgula decimal;
- utiliza data no formato brasileiro;
- pode ser aberto no Excel em português.

Cabeçalho:

```text
id;tipo;categoria;descricao;valorEmReal;dataHora;status;motivoRejeicao;usuarioDestinoId
```

## Importação CSV do User Service

Cabeçalho esperado:

```csv
name,email,password
```

Exemplo:

```csv
name,email,password
Maria Silva,maria@example.com,Senha123
João Souza,joao@example.com,OutraSenha456
```

Regras:

- máximo de 1.000 linhas;
- linhas válidas são importadas;
- linhas inválidas não interrompem o arquivo;
- erros são informados por linha;
- e-mails duplicados são rejeitados;
- arquivos com BOM UTF-8 são aceitos;
- CSV malformado retorna HTTP 400.

Arquivo de exemplo:

```text
user-service/examples/users-import-example.csv
```

## Tratamento de erros

Formato padrão:

```json
{
  "status": 400,
  "message": "Mensagem do erro",
  "erros": {}
}
```

Principais códigos:

- `400`: requisição ou parâmetros inválidos;
- `401`: autenticação necessária ou credenciais inválidas;
- `404`: recurso não encontrado;
- `405`: método HTTP não permitido;
- `409`: conflito de dados;
- `413`: arquivo maior que o permitido;
- `415`: tipo de conteúdo não suportado;
- `500`: erro interno inesperado;
- `503`: serviço externo indisponível.

## Testes

User Service:

```powershell
.\user-service\mvnw.cmd -f .\user-service\pom.xml test
```

Transaction Service:

```powershell
.\transaction-service\mvnw.cmd -f .\transaction-service\pom.xml test
```

## Observações

- As contas financeiras são simuladas pela MockAPI.
- O identificador `userId` da conta deve corresponder ao usuário do MS1.
- As cotações são consultadas na BrasilAPI.
- A indisponibilidade da BrasilAPI não impede o processamento financeiro.
- As transações são mantidas para preservar o histórico.
- A correção é feita por uma nova transação compensatória.