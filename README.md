# MecâniQA API

API REST desenvolvida para o projeto MecâniQA, responsável pelo gerenciamento de Peças e Serviços.
O projeto foi desenvolvido utilizando Java 21 e Spring Boot, implementando endpoints REST para operações de cadastro, consulta, atualização e exclusão de dados.
Os dados são armazenados em memória, utilizando o padrão de projeto Singleton nos repositórios.

---

## Sobre o Projeto

A MecâniQA API foi desenvolvida como uma API fundacional para representar parte da estrutura de gerenciamento de uma oficina mecânica.
A aplicação possui dois recursos principais:

- 🔧 **Peças**
- 🛠️ **Serviços**

Cada recurso possui endpoints para realizar operações CRUD.
A API também implementa o tratamento de diferentes respostas HTTP, incluindo:

- `200 OK`
- `201 Created`
- `204 No Content`
- `400 Bad Request`
- `404 Not Found`

---

## Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.5.5**
- **Spring Web**
- **Gradle**
- **Postman**
- **API REST**
- **JSON**

---

## Arquitetura do Projeto

A aplicação está organizada utilizando uma estrutura simples dividida em:

```text
src/main/java/com/mecaniqa/api/
│
├── MecaniQaApiApplication.java
│
├── controller/
│   ├── PecaController.java
│   └── ServicoController.java
│
├── model/
│   ├── Peca.java
│   ├── Servico.java
│   └── CategoriaPeca.java
│
└── repository/
    ├── PecaRepository.java
    └── ServicoRepository.java
```

Além do código principal, o projeto possui:

```text
documentacao/
└── diagrama-classes.md

postman/
└── mecaniQA-api.postman_collection.json
```

---

##  Modelos da Aplicação

### 🔧 Peça
A classe `Peca` representa uma peça cadastrada no sistema.

| Atributo | Tipo |
|---|---|
| `codigo` | `Long` |
| `codigoBarras` | `String` |
| `fornecedorMarca` | `String` |
| `quantidadeEstoque` | `Integer` |
| `precoCusto` | `Double` |
| `precoVenda` | `Double` |
| `dataCadastro` | `LocalDateTime` |
| `dataUltimaAtualizacao` | `LocalDateTime` |
| `tamanho` | `String` |
| `cor` | `String` |
| `categoria` | `CategoriaPeca` |

### 🛠️ Serviço
A classe `Servico` representa um serviço cadastrado no sistema.

| Atributo | Tipo |
|---|---|
| `codigo` | `Long` |
| `nome` | `String` |
| `tempoEstimadoMinutos` | `Integer` |
| `custoTabelado` | `Double` |
| `dataCriacao` | `LocalDateTime` |
| `dataUltimaAtualizacao` | `LocalDateTime` |

### Categorias de Peças
As categorias de peças são representadas pelo Enum `CategoriaPeca`:
- `MOTOR`
- `SUSPENSAO`
- `FREIOS`
- `ELETRICA`
- `ACESSORIOS`

---

## Endpoints da API

A aplicação possui endpoints para gerenciamento de Peças e Serviços.
A URL base da aplicação é: `http://localhost:8080`

### 🔩 Endpoints de Peças

#### ➕ Cadastrar Peça
`POST /api/pecas`

**Exemplo de requisição:**
```json
{
  "codigoBarras": "7891234567890",
  "fornecedorMarca": "Bosch",
  "quantidadeEstoque": 10,
  "precoCusto": 50.0,
  "precoVenda": 80.0,
  "tamanho": "M",
  "cor": "Preto",
  "categoria": "ELETRICA"
}
```
**Resposta esperada:** `201 Created`
Ao cadastrar uma nova peça:
- A peça recebe um código automático.
- A data de cadastro é registrada.
- A data da última atualização é registrada.
- A peça é gravada na memória através do `PecaRepository`.
- O recurso criado é retornado.

####  Listar Peças
`GET /api/pecas`
- **Resposta esperada:** `200 OK`
- Retorna todas as peças cadastradas na memória.

#### Buscar Peça por Código
`GET /api/pecas/{codigo}`
- **Exemplo:** `GET /api/pecas/1`
- **Resposta esperada:** `200 OK` (Caso a peça seja encontrada).

#### Buscar Peça Inexistente
`GET /api/pecas/{codigo}`
- **Exemplo:** `GET /api/pecas/999999`
- Caso o código informado não esteja cadastrado no Singleton, a API retorna: `404 Not Found` (Essa validação é realizada manualmente no `PecaController`).

#### Atualizar Peça
`PUT /api/pecas/{codigo}`

**Exemplo de requisição:**
```json
{
  "codigoBarras": "7891234567890",
  "fornecedorMarca": "Bosch",
  "quantidadeEstoque": 15,
  "precoCusto": 55.0,
  "precoVenda": 90.0,
  "tamanho": "M",
  "cor": "Preto",
  "categoria": "ELETRICA"
}
```
- **Resposta esperada:** `200 OK` (A data de cadastro original é mantida e a data da última atualização é modificada).
- **Caso a peça não exista:** `404 Not Found`

#### 🗑️ Excluir Peça
`DELETE /api/pecas/{codigo}`
- **Resposta esperada:** `204 No Content`
- **Caso a peça não exista:** `404 Not Found`

---

### 🛠️ Endpoints de Serviços

#### ➕ Cadastrar Serviço
`POST /api/servicos`

**Exemplo de requisição:**
```json
{
  "nome": "Troca de óleo",
  "tempoEstimadoMinutos": 60,
  "custoTabelado": 150.0
}
```
**Resposta esperada:** `201 Created`
Ao cadastrar um serviço:
- É gerado um código automático.
- A data de criação é registrada.
- A data da última atualização é registrada.
- O serviço é gravado em memória através do `ServicoRepository`.

#### Listar Serviços
`GET /api/servicos`
- **Resposta esperada:** `200 OK`

####  Buscar Serviço por Código
`GET /api/servicos/{codigo}`
- **Exemplo:** `GET /api/servicos/1`
- **Resposta esperada:** `200 OK` (Caso o serviço exista) ou `404 Not Found` (Caso contrário).

#### Atualizar Serviço
`PUT /api/servicos/{codigo}`

**Exemplo de requisição:**
```json
{
  "nome": "Troca de óleo completa",
  "tempoEstimadoMinutos": 90,
  "custoTabelado": 200.0
}
```
- **Resposta esperada:** `200 OK`
- **Caso o serviço não exista:** `404 Not Found`

#### 🗑️ Excluir Serviço
`DELETE /api/servicos/{codigo}`
- **Resposta esperada:** `204 No Content`
- **Caso o serviço não exista:** `404 Not Found`

---

##  Resumo das Rotas

### Peças
| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/pecas` | Cadastrar uma peça |
| `GET` | `/api/pecas` | Listar todas as peças |
| `GET` | `/api/pecas/{codigo}` | Buscar uma peça |
| `PUT` | `/api/pecas/{codigo}` | Atualizar uma peça |
| `DELETE`| `/api/pecas/{codigo}` | Excluir uma peça |

### Serviços
| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/servicos` | Cadastrar um serviço |
| `GET` | `/api/servicos` | Listar todos os serviços |
| `GET` | `/api/servicos/{codigo}` | Buscar um serviço |
| `PUT` | `/api/servicos/{codigo}` | Atualizar um serviço |
| `DELETE`| `/api/servicos/{codigo}` | Excluir um serviço |

---

## Armazenamento em Memória

A aplicação não utiliza banco de dados. Os dados são armazenados temporariamente em memória utilizando os repositórios:
- `PecaRepository`
- `ServicoRepository`

Os dois repositórios utilizam o padrão de projeto **Singleton**. Isso garante que exista apenas uma instância de cada repositório durante a execução da aplicação.

**Exemplo conceitual:**
```text
Controller
    ↓
Repository Singleton
    ↓
Lista em Memória
```
Os dados permanecem disponíveis enquanto a aplicação estiver em execução. Ao reiniciar a API, os dados armazenados em memória são perdidos.

---

## 🧪 Validações Realizadas

### POST — 201 Created
Foi validada a execução da requisição `POST /api/pecas`. O fluxo ocorre da seguinte forma:
`POST` → `PecaController` → `PecaRepository.getInstance()` → `save()` → Gravação em `ArrayList` na memória → `201 Created`.
A peça cadastrada recebe um código automático e pode ser consultada posteriormente.

### GET — 404 Not Found
Também foi validada a tentativa de buscar uma peça que ainda não foi cadastrada no Singleton (Exemplo: `GET /api/pecas/999999`). Quando o código não é encontrado, `404 Not Found` é retornado pela API.

---

## 📬 Collection do Postman

O projeto possui uma Collection do Postman com as rotas da API.
**Arquivo:** `postman/mecaniQA-api.postman_collection.json`

**Como importar:**
1. Abra o Postman.
2. Clique em **Import**.
3. Selecione o arquivo `mecaniQA-api.postman_collection.json`.
4. Execute as requisições.

A Collection contém as rotas de Peças e Serviços, englobando Cadastro, Listagem, Busca por código, Atualização e Exclusão.

---

##  Diagrama de Classes UML

O projeto possui um Diagrama de Classes representando a estrutura principal da aplicação.
**Arquivo:** `documentacao/diagrama-classes.md`

O diagrama representa as seguintes classes:
```text
PecaController
ServicoController
        ↓
PecaRepository
ServicoRepository
        ↓
Peca
Servico
        ↓
CategoriaPeca
```
Os repositórios são identificados como estruturas responsáveis pelo armazenamento dos dados em memória utilizando o padrão Singleton.

---

## Como Executar o Projeto

**Pré-requisitos:**
É necessário possuir instalado:
- Java 21
- Git

**1. Clonar o Repositório**
```bash
git clone <URL_DO_REPOSITORIO>
```

**2. Acessar a Pasta do Projeto**
```bash
cd mecaniQA-api-teresina
```

**3. Executar a Aplicação**
- **Windows:**
  ```cmd
  gradlew.bat bootRun
  ```
- **Linux ou macOS:**
  ```bash
  ./gradlew bootRun
  ```

---

## Servidor

Por padrão, a aplicação é executada na porta `8080`.
Portanto, a URL base é: `http://localhost:8080`

---

## Códigos HTTP Utilizados

| Código | Status | Utilização |
|---|---|---|
| `200` | OK | Consulta ou atualização realizada com sucesso |
| `201` | Created | Novo recurso criado com sucesso |
| `204` | No Content | Recurso excluído com sucesso |
| `400` | Bad Request | Requisição inválida |
| `404` | Not Found | Recurso não encontrado |

---

##  Estrutura Completa do Projeto

```text
mecaniQA-api-teresina/
│
├── apresentacoes/
│
├── documentacao/
│   └── diagrama-classes.md
│
├── postman/
│   └── mecaniQA-api.postman_collection.json
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── mecaniqa/
│       │           └── api/
│       │               ├── MecaniQaApiApplication.java
│       │               │
│       │               ├── controller/
│       │               │   ├── PecaController.java
│       │               │   └── ServicoController.java
│       │               │
│       │               ├── model/
│       │               │   ├── Peca.java
│       │               │   ├── Servico.java
│       │               │   └── CategoriaPeca.java
│       │               │
│       │               └── repository/
│       │                   ├── PecaRepository.java
│       │                   └── ServicoRepository.java
│       │
│       └── resources/
│           └── application.properties
│
├── build.gradle.kts
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
└── README.md
```

---

## Funcionalidades Implementadas
- [x] Cadastro de peças;
- [x] Listagem de peças;
- [x] Busca de peça por código;
- [x] Atualização de peças;
- [x] Exclusão de peças;
- [x] Cadastro de serviços;
- [x] Listagem de serviços;
- [x] Busca de serviço por código;
- [x] Atualização de serviços;
- [x] Exclusão de serviços;
- [x] Geração automática de códigos;
- [x] Registro automático de datas;
- [x] Armazenamento em memória;
- [x] Implementação do padrão Singleton;
- [x] Retornos HTTP adequados;
- [x] Tratamento de recursos inexistentes;
- [x] Collection para testes no Postman;
- [x] Diagrama de Classes UML.

