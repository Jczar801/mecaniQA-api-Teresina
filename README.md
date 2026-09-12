# MecâniQA API - Teresina

API REST em Java 21, Spring Boot 3.5.5 e Gradle. A OAT 2 evolui o CRUD de peças e serviços da OAT 1 com ordens de serviço e pedidos de peças.

## Execução e testes

É necessário um JDK 21. Configure `JAVA_HOME` para esse JDK.

```powershell
.\gradlew.bat bootRun
.\gradlew.bat test
```

No Linux/macOS, use `./gradlew bootRun` e `./gradlew test`. URL base: `http://localhost:8080`.

Os dados permanecem exclusivamente em memória e são perdidos ao reiniciar a aplicação. Os repositórios usam Singleton estático; não há banco de dados nem Lombok. Construtores e acessores são explícitos.

## Endpoints

| Recurso | Método e rota | Resultado |
|---|---|---|
| Peças | `POST /api/pecas` | Cadastro, 201 |
| Peças | `GET /api/pecas` | Lista, 200 |
| Peças | `GET /api/pecas/{codigo}` | Consulta, 200/404 |
| Peças | `PUT /api/pecas/{codigo}` | Atualização, 200/404 |
| Peças | `DELETE /api/pecas/{codigo}` | Exclusão, 204/404 |
| Serviços | `POST /api/servicos` | Cadastro, 201 |
| Serviços | `GET /api/servicos` | Lista, 200 |
| Serviços | `GET /api/servicos/{codigo}` | Consulta, 200/404 |
| Serviços | `PUT /api/servicos/{codigo}` | Atualização, 200/404 |
| Serviços | `DELETE /api/servicos/{codigo}` | Exclusão, 204/404 |
| US01 | `POST /api/ordens-servico` | Cria OS, 201 |
| US02 | `PATCH /api/ordens-servico/{codigo}/status` | Altera status da OS, 200 |
| US03 | `POST /api/pedidos-pecas` | Cria pedido, 201 |
| US04 | `POST /api/pedidos-pecas/{codigo}/itens` | Adiciona peça e quantidade, 200 |
| US05 | `PATCH /api/pedidos-pecas/{codigo}/status` | Altera status do pedido, 200 |

Entradas inválidas retornam 400; referências a peças, serviços, pedidos ou ordens inexistentes retornam 404. Quantidades devem ser inteiras positivas. A repetição de uma peça soma sua quantidade no mesmo item; soma acima do limite de `Integer` retorna 400 sem alterar o pedido. A criação com itens só é salva após validar todos eles.

## Exemplo completo

Substitua os códigos de exemplo pelos retornados nos cadastros. A coleção `postman/MecaniQA_API_OAT2.postman_collection.json` automatiza essa sequência e armazena os códigos recebidos em variáveis.

1. `POST /api/pecas`

```json
{"codigoBarras":"7891234567890","fornecedorMarca":"Bosch","quantidadeEstoque":10,"precoCusto":50,"precoVenda":80,"tamanho":"M","cor":"Preto","categoria":"MOTOR"}
```

Categorias disponíveis: `MOTOR`, `SUSPENSAO`, `FREIOS`, `ELETRICA`, `ACESSORIOS`.

2. `POST /api/servicos`

```json
{"nome":"Revisão","tempoEstimadoMinutos":60,"custoTabelado":150}
```

3. `POST /api/pedidos-pecas`

```json
{"itens":[{"codigoPeca":1,"quantidade":2}]}
```

Também é possível enviar `{}` para iniciar um pedido vazio. O status inicial é `ORCANDO`.

4. `POST /api/pedidos-pecas/1/itens`

```json
{"codigoPeca":1,"quantidade":3}
```

5. `PATCH /api/pedidos-pecas/1/status`

```json
{"status":"PAGO_FATURADO"}
```

6. `POST /api/ordens-servico`

```json
{"descricao":"Revisão do veículo","codigosServicos":[1],"codigosPedidosPecas":[1]}
```

Descrição e associações são opcionais, pois o enunciado não define campos obrigatórios de cliente/veículo. O Builder inicia a OS em `ABERTO`, registra datas e reúne os serviços e pedidos informados. Códigos repetidos nas listas de associações são tratados como uma associação única. Peças são vinculadas à OS por meio dos pedidos e seus itens.

7. `PATCH /api/ordens-servico/1/status`

```json
{"status":"EM_EXECUCAO"}
```

| OS | Pedido de peças |
|---|---|
| `ABERTO` (Aberto) | `ORCANDO` (Orçando) |
| `PENDENTE_DE_PAGAMENTO` (Pendente de Pagamento) | `PENDENTE_DE_PAGAMENTO` (Pendente de Pagamento) |
| `PAGO` (Pago) | `PAGO_FATURADO` (Pago/Faturado) |
| `EM_EXECUCAO` (Em Execução) | `ENTREGUE` (Entregue) |
| `EXECUTADO` (Executado) | |

Qualquer status do respectivo enum pode ser selecionado: o enunciado não determina uma sequência de transições. A adição de itens não movimenta estoque nem processa pagamentos, operações não solicitadas nesta OAT.

## DTOs, mappers e modelo

Todos os corpos de entrada e saída dos controllers são DTOs. `PecaMapper` e `ServicoMapper` convertem os cadastros existentes nos dois sentidos. Código e datas desses DTOs são somente de leitura no JSON e são gerados/controlados pelo servidor.

`CriarOrdemServicoDTO` e `CriarPedidoPecasDTO` recebem dados de criação; DTOs específicos recebem itens e alterações de status. `OrdemServicoDTO`, `PedidoPecasDTO` e `ItemPedidoDTO` representam as respostas. Os mappers fazem as conversões, inclusive as associações, sem expor entidades nos objetos aninhados.

`ItemPedido` é a entidade associativa: referencia um `PedidoPecas`, uma `Peca` e a quantidade. O DTO omite a referência de volta ao pedido, evitando recursão no JSON. `OrdemServico.Builder` é a única forma de construir uma OS; cria instâncias independentes e copia suas listas de associações.

## Documentação e entrega

- [Diagrama de classes atualizado](documentacao/diagrama-classes.md): entidades, enums, DTOs, mappers, controllers, Singletons e Builder.
- [Diagrama de atividade](documentacao/diagrama-atividade.md): lógica do controller de `PATCH /api/ordens-servico/{codigo}/status`.
- Apresentação: `apresentacoes/mecaniQA_api_oat2_Teresina.pdf`, no modelo indicado no enunciado.
- Testes: `src/test/java/com/mecaniqa/api/Oat2IntegrationTest.java` cobre US01-US05, status, associações, erros, proteção de metadados, Builder e regressão do CRUD.

A entrega avaliada deve estar na branch `main` deste repositório. O PDF também deve ser submetido pela equipe no formulário do Blackboard indicado no enunciado. A apresentação oral tem duração máxima de sete minutos.
