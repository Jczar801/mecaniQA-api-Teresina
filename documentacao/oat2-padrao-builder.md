# OAT 2 - Padrão Builder na Ordem de Serviço

Registro do Arquiteto para o encontro de implementação do padrão criacional na
construção da Ordem de Serviço (OS).

## Fatos extraídos da OAT 2

### Obrigatoriedade criacional

> Segundo a seção de Padrões de Projeto, qual padrão deve obrigatoriamente ser
> utilizado na construção de uma Ordem de Serviço (OS)?

**Builder.** A seção de Padrões de Projeto da OAT 2 exige o uso do Builder na
construção da OS. Como consequência, `new OrdemServico(...)` deixa de ser uma
forma válida de instanciar a entidade: o único ponto de entrada é
`OrdemServico.builder()`, e a instância só nasce ao final da cadeia, no
`build()`.

Implementação: [`OrdemServico`](../src/main/java/com/mecaniqa/api/model/OrdemServico.java).
Uso na aplicação: [`OrdemServicoMapper.toModel`](../src/main/java/com/mecaniqa/api/mapper/OrdemServicoMapper.java),
chamado por `POST /api/ordens-servico`.

### A persistência

> Houve alguma alteração nos repositories em relação à atividade passada?

**Não houve mudança de mecanismo.** A persistência continua exclusivamente em
memória, com repositórios Singleton, sem banco de dados, sem JPA e sem Lombok.
Cada repositório mantém o mesmo formato da OAT 1: construtor privado, campo
estático `INSTANCE`, `getInstance()`, uma `List` em memória como armazenamento e
um `AtomicLong` para gerar os códigos.

O que mudou foi apenas a quantidade de repositórios: `OrdemServicoRepository` e
`PedidoPecasRepository` foram acrescentados aos já existentes `PecaRepository` e
`ServicoRepository`, seguindo exatamente o mesmo padrão. Os dados continuam
sendo perdidos ao reiniciar a aplicação.

## Brainstorm investigativo

### A anatomia do Builder sem Lombok

**Onde fica a classe aninhada.** `Builder` é declarada como classe estática
aninhada dentro da própria `OrdemServico` (`public static class Builder`). Ela
precisa ser `static` porque é usada para criar a primeira `OrdemServico` — se
fosse uma classe interna de instância, seria necessário já ter uma OS para
poder construir outra. Estar aninhada também é o que lhe dá acesso ao
construtor privado de `OrdemServico`, sem expor esse construtor ao resto do
sistema.

**Como se inicia a cadeia.** Um método estático `OrdemServico.builder()`
devolve um `Builder` novo. O construtor do próprio `Builder` é privado, de modo
que nem mesmo `new OrdemServico.Builder()` funciona: `builder()` é a única
porta de entrada.

**Como os métodos encadeiam.** Cada método de configuração guarda o valor num
campo do `Builder` e devolve `this` — a mesma instância do builder — em vez de
`void`. É isso que permite escrever `OrdemServico.builder().descricao(...)
.adicionarServico(...).build()`, porque cada chamada devolve um objeto do tipo
`Builder`, sobre o qual a chamada seguinte é feita.

```java
OrdemServico ordem = OrdemServico.builder()
        .descricao("Revisão do veículo")
        .adicionarServico(servico)
        .adicionarPedidoPecas(pedido)
        .build();
```

**Coleções.** As listas de `Servico` e `PedidoPecas` são acumuladas em `ArrayList`
mutáveis dentro do `Builder` e copiadas com `List.copyOf` no construtor da OS.
Assim cada `build()` produz uma OS independente, com listas imutáveis, e o
mesmo builder pode ser reaproveitado sem que alterações posteriores vazem para
as ordens já construídas.

**Campos controlados pelo servidor.** `status`, `dataCriacao` e
`dataUltimaAtualizacao` não são expostos no `Builder`: são definidos pelo
construtor privado (`ABERTO` e o instante atual). O cliente da API não consegue
forjá-los.

### O construtor privado

O construtor de `OrdemServico` passa a ser `private` e a receber apenas o
`Builder` por três motivos:

1. **Obrigar a passagem pelo Builder.** Sendo privado, nenhum código fora de
   `OrdemServico` consegue invocá-lo. A única classe que enxerga esse construtor
   é a `Builder` aninhada, que o chama dentro de `build()`. O padrão deixa de ser
   uma convenção e passa a ser garantido pelo compilador.
2. **Evitar o telescoping constructor.** Em vez de várias sobrecargas com listas
   crescentes de parâmetros posicionais (e o risco de trocar argumentos do mesmo
   tipo), existe um único parâmetro. Os valores chegam nomeados, pelos métodos do
   builder, e os opcionais simplesmente não são chamados.
3. **Construir o objeto já completo e consistente.** A OS só existe depois que
   todos os dados foram reunidos no builder; os campos podem então ser `final` e
   as coleções copiadas para versões imutáveis, sem estados intermediários
   inválidos.

## Cenário de teste (QA)

> O código deve falhar (em tempo de compilação) se o Desenvolvedor Piloto tentar
> criar uma OS utilizando o operador `new OrdemServico()` em vez de passar pelo
> `OrdemServico.builder()`.

**Resultado: falha de compilação, conforme exigido.** `OrdemServico` declara um
único construtor, `private OrdemServico(OrdemServico.Builder)`. Como existe um
construtor declarado, o Java não gera o construtor padrão sem argumentos; e o
que existe é privado e invisível fora da classe. O `javac` interrompe a
compilação — o erro acontece antes de haver bytecode, não é uma exceção em
tempo de execução. O mesmo vale para `new OrdemServico.Builder()`, já que o
construtor do builder também é privado.

```java
OrdemServico a = new OrdemServico();                // não compila
OrdemServico b = new OrdemServico.Builder().build(); // não compila
OrdemServico c = OrdemServico.builder().build();     // forma correta
```

Saída do `javac` (JDK 21) para o trecho acima:

```text
error: constructor OrdemServico in class OrdemServico cannot be applied to given types;
        OrdemServico a = new OrdemServico();
                         ^
  required: Builder
  found:    no arguments
  reason: actual and formal argument lists differ in length
error: Builder() has private access in Builder
        OrdemServico b = new OrdemServico.Builder().build();
                         ^
2 errors
```

A terceira linha, `OrdemServico.builder().build()`, é a única que compila.

**Guarda automatizada.** Uma falha de compilação não pode ser capturada por um
teste JUnit — o teste que a contivesse também não compilaria. O teste
`ordemServicoSoPodeSerConstruidaPeloBuilder`, em
[`Oat2IntegrationTest`](../src/test/java/com/mecaniqa/api/Oat2IntegrationTest.java),
verifica por reflexão as condições que produzem essa falha e impede que uma
refatoração futura as remova em silêncio:

- `OrdemServico` declara exatamente um construtor;
- esse construtor é `private` e recebe `OrdemServico.Builder`;
- não existe construtor sem argumentos (`getDeclaredConstructor()` lança
  `NoSuchMethodException`);
- `Builder` é `static`, aninhada em `OrdemServico`, e seu construtor é `private`;
- os métodos de configuração devolvem a própria instância do builder;
- a OS resultante nasce em `ABERTO` e com `dataCriacao` preenchida.

O teste `builderCriaInstanciasIndependentesERepositoriosSaoSingletons`
complementa, cobrindo a independência entre duas OS geradas pelo mesmo builder,
a imutabilidade das listas e a permanência dos repositórios como Singletons.
