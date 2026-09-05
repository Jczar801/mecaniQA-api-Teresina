# Diagrama de Classes - OAT 2

```mermaid
classDiagram
direction LR
    class CategoriaPeca {
      <<enumeration>>
      MOTOR
      SUSPENSAO
      FREIOS
      ELETRICA
      ACESSORIOS
    }
    class ItemPedido {
      -PedidoPecas pedido
      -Peca peca
      -Integer quantidade
    }
    class OrdemServico {
      -Long codigo
      -String descricao
      -StatusOrdemServico status
      -LocalDateTime dataCriacao
      -LocalDateTime dataUltimaAtualizacao
      -List~Servico~ servicos
      -List~PedidoPecas~ pedidosPecas
      -OrdemServico(Builder)
      +builder()$
    }
    class Peca {
      -Long codigo
      -String codigoBarras
      -String fornecedorMarca
      -Integer quantidadeEstoque
      -Double precoCusto
      -Double precoVenda
      -LocalDateTime dataCadastro
      -LocalDateTime dataUltimaAtualizacao
      -String tamanho
      -String cor
      -CategoriaPeca categoria
    }
    class PedidoPecas {
      -Long codigo
      -StatusPedidoPecas status
      -LocalDateTime dataCriacao
      -LocalDateTime dataUltimaAtualizacao
      -List~ItemPedido~ itens
    }
    class Servico {
      -Long codigo
      -String nome
      -Integer tempoEstimadoMinutos
      -Double custoTabelado
      -LocalDateTime dataCriacao
      -LocalDateTime dataUltimaAtualizacao
    }
    class StatusOrdemServico {
      <<enumeration>>
      ABERTO
      PENDENTE_DE_PAGAMENTO
      PAGO
      EM_EXECUCAO
      EXECUTADO
    }
    class StatusPedidoPecas {
      <<enumeration>>
      ORCANDO
      PENDENTE_DE_PAGAMENTO
      PAGO_FATURADO
      ENTREGUE
    }
    class AtualizarStatusOrdemServicoDTO {
      <<DTO>>
      -StatusOrdemServico status
    }
    class AtualizarStatusPedidoPecasDTO {
      <<DTO>>
      -StatusPedidoPecas status
    }
    class CriarOrdemServicoDTO {
      <<DTO>>
      -String descricao
      -List~Long~ codigosServicos
      -List~Long~ codigosPedidosPecas
    }
    class CriarPedidoPecasDTO {
      <<DTO>>
      -List~ItemPedidoRequestDTO~ itens
    }
    class ItemPedidoDTO {
      <<DTO>>
      -Long codigoPeca
      -Integer quantidade
    }
    class ItemPedidoRequestDTO {
      <<DTO>>
      -Long codigoPeca
      -Integer quantidade
    }
    class OrdemServicoDTO {
      <<DTO>>
      -Long codigo
      -String descricao
      -StatusOrdemServico status
      -LocalDateTime dataCriacao
      -LocalDateTime dataUltimaAtualizacao
      -List~ServicoDTO~ servicos
      -List~PedidoPecasDTO~ pedidosPecas
    }
    class PecaDTO {
      <<DTO>>
      -Long codigo
      -String codigoBarras
      -String fornecedorMarca
      -Integer quantidadeEstoque
      -Double precoCusto
      -Double precoVenda
      -LocalDateTime dataCadastro
      -LocalDateTime dataUltimaAtualizacao
      -String tamanho
      -String cor
      -CategoriaPeca categoria
    }
    class PedidoPecasDTO {
      <<DTO>>
      -Long codigo
      -StatusPedidoPecas status
      -LocalDateTime dataCriacao
      -LocalDateTime dataUltimaAtualizacao
      -List~ItemPedidoDTO~ itens
    }
    class ServicoDTO {
      <<DTO>>
      -Long codigo
      -String nome
      -Integer tempoEstimadoMinutos
      -Double custoTabelado
      -LocalDateTime dataCriacao
      -LocalDateTime dataUltimaAtualizacao
    }
    class OrdemServicoMapper {
      <<Mapper>>
      +toModel()
      +toDTO()
    }
    class PecaMapper {
      <<Mapper>>
      +toModel()
      +toDTO()
    }
    class PedidoPecasMapper {
      <<Mapper>>
      +toModel()
      +toDTO()
    }
    class ServicoMapper {
      <<Mapper>>
      +toModel()
      +toDTO()
    }
    class OrdemServicoRepository {
      <<Singleton>>
      -OrdemServicoRepository INSTANCE$
      +save(OrdemServico) OrdemServico
      +findById(Long) Optional
      -OrdemServicoRepository()
      +getInstance()$
    }
    class PecaRepository {
      +findAll() List
      +deleteById(Long) boolean
      <<Singleton>>
      -PecaRepository INSTANCE$
      +save(Peca) Peca
      +findById(Long) Optional
      -PecaRepository()
      +getInstance()$
    }
    class PedidoPecasRepository {
      <<Singleton>>
      -PedidoPecasRepository INSTANCE$
      +save(PedidoPecas) PedidoPecas
      +findById(Long) Optional
      -PedidoPecasRepository()
      +getInstance()$
    }
    class ServicoRepository {
      +findAll() List
      +deleteById(Long) boolean
      <<Singleton>>
      -ServicoRepository INSTANCE$
      +save(Servico) Servico
      +findById(Long) Optional
      -ServicoRepository()
      +getInstance()$
    }
    class OrdemServicoController {
    }
    class PecaController {
    }
    class PedidoPecasController {
    }
    class ServicoController {
    }
    class OrdemServicoBuilder {
      <<Builder>>
      -String descricao
      -List~Servico~ servicos
      -List~PedidoPecas~ pedidosPecas
      +descricao(String) Builder
      +adicionarServico(Servico) Builder
      +adicionarPedidoPecas(PedidoPecas) Builder
      +build() OrdemServico
    }
    Peca --> CategoriaPeca
    OrdemServico --> StatusOrdemServico
    PedidoPecas --> StatusPedidoPecas
    PedidoPecas "1" *-- "0..*" ItemPedido : itens
    ItemPedido "0..*" --> "1" Peca : peca
    OrdemServico "0..*" --> "0..*" Servico : servicos
    OrdemServico "0..*" --> "0..*" PedidoPecas : pedidosPecas
    OrdemServicoBuilder ..> OrdemServico : constroi
    OrdemServicoMapper ..> OrdemServicoBuilder : usa
    OrdemServicoDTO --> ServicoDTO
    OrdemServicoDTO --> PedidoPecasDTO
    PedidoPecasDTO --> ItemPedidoDTO
    CriarPedidoPecasDTO --> ItemPedidoRequestDTO
    AtualizarStatusOrdemServicoDTO --> StatusOrdemServico
    AtualizarStatusPedidoPecasDTO --> StatusPedidoPecas
    PecaDTO --> CategoriaPeca
    OrdemServicoDTO --> StatusOrdemServico
    PedidoPecasDTO --> StatusPedidoPecas
    PecaController ..> PecaRepository
    PecaController ..> PecaMapper
    PecaRepository "1" o-- "0..*" Peca
    PecaMapper ..> PecaDTO
    PecaMapper ..> Peca
    PecaController ..> PecaDTO
    ServicoController ..> ServicoRepository
    ServicoController ..> ServicoMapper
    ServicoRepository "1" o-- "0..*" Servico
    ServicoMapper ..> ServicoDTO
    ServicoMapper ..> Servico
    ServicoController ..> ServicoDTO
    OrdemServicoController ..> OrdemServicoRepository
    OrdemServicoController ..> OrdemServicoMapper
    OrdemServicoRepository "1" o-- "0..*" OrdemServico
    OrdemServicoMapper ..> OrdemServicoDTO
    OrdemServicoMapper ..> OrdemServico
    OrdemServicoController ..> OrdemServicoDTO
    PedidoPecasController ..> PedidoPecasRepository
    PedidoPecasController ..> PedidoPecasMapper
    PedidoPecasRepository "1" o-- "0..*" PedidoPecas
    PedidoPecasMapper ..> PedidoPecasDTO
    PedidoPecasMapper ..> PedidoPecas
    PedidoPecasController ..> PedidoPecasDTO
    OrdemServicoController ..> ServicoRepository
    OrdemServicoController ..> PedidoPecasRepository
    OrdemServicoController ..> CriarOrdemServicoDTO
    OrdemServicoController ..> AtualizarStatusOrdemServicoDTO
    PedidoPecasController ..> PecaRepository
    PedidoPecasController ..> CriarPedidoPecasDTO
    PedidoPecasController ..> ItemPedidoRequestDTO
    PedidoPecasController ..> AtualizarStatusPedidoPecasDTO
    OrdemServicoMapper ..> CriarOrdemServicoDTO
    PedidoPecasMapper ..> CriarPedidoPecasDTO
    PedidoPecasMapper ..> ItemPedidoRequestDTO
    PedidoPecasMapper ..> ItemPedidoDTO
    PedidoPecasMapper ..> ItemPedido
```

`OrdemServicoBuilder` representa a classe estática interna `OrdemServico.Builder`. As associações usam as entidades existentes em memória. Construtores e acessores estão explícitos no código; getters/setters repetitivos foram omitidos do desenho. Os campos e métodos estáticos dos Singletons garantem uma instância de cada repositório.

[Versão PDF do diagrama completo](../mecaniQA_api_Teresina.pdf).
