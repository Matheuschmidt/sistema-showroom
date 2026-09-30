# CONTEXTO_PROJETO.md

# Sistema Showroom --- Contexto do Projeto

## 1. Objetivo deste documento

Este arquivo fornece ao agente de desenvolvimento o contexto atual do
projeto.

Ele deve ser lido junto com `REGRAS_NEGOCIO.md` antes de realizar
alterações relevantes.

- `REGRAS_NEGOCIO.md` = regras que o sistema deve respeitar.
- `CONTEXTO_PROJETO.md` = estado, estrutura e decisões conhecidas do
  projeto.

------------------------------------------------------------------------

## 2. Estado atual

O projeto está sendo desenvolvido inicialmente como uma **aplicação Java
de console**.

A intenção é construir e consolidar primeiro a lógica do sistema e suas
regras de negócio antes de evoluir para tecnologias/arquiteturas mais
avançadas.

O projeto já possui uma estrutura organizada em pacotes dentro de `src`.

Estado em 30/09/2026: os fluxos de ADMIN, LOJAS e FÁBRICA estão
implementados no console, com cálculos de peso comercial, peso de
produção e valor da venda, status do pedido, edição, cancelamento,
clientes por loja e faturamento. Ver seção 5 para o detalhamento e as
pendências.

------------------------------------------------------------------------

## 3. Tecnologia

Tecnologia atualmente definida:

-   Java 17
-   IntelliJ IDEA
-   Git/GitHub
-   Aplicação inicialmente em console

Não atualizar a versão do Java nem migrar a arquitetura sem necessidade
e sem verificar a compatibilidade com o projeto existente.

------------------------------------------------------------------------

## 4. Estrutura atual

``` text
src/
├── Main.java                  login e direcionamento para o menu do perfil
├── calculo/
│   ├── IndiceAro              tabela oficial de índices (8 a 40)
│   └── CalculadoraPesoProducao peso de produção por unidade + arredondamento
├── catalogo/
│   └── CatalogoModelos        modelos cadastrados (peso de UMA unidade)
├── dominio/
│   ├── cliente/Cliente        cliente pertence a uma loja
│   ├── produto/               Modelo, Alianca (unidade), TeorOuro, TipoAro
│   └── usuario/               Perfil, Admin, Loja, Fabrica
├── impressao/
│   ├── ImpressaoPedido        via do cliente e via da fábrica (texto)
│   └── Formatacao             números, pesos e moeda em pt-BR
├── menu/
│   ├── Entrada                leitura validada do console (aceita vírgula)
│   ├── MenuAdmin, MenuLoja, MenuFabrica
├── sistema/
│   └── SistemaShowroom        perfis, cotação vigente, clientes, pedidos, faturamento
└── venda/
    ├── PedidoVenda            unidades, valores, status, edição, cancelamento
    ├── CotacaoOuro            imutável (congelada na venda)
    ├── Acrescimo, StatusPedido

test/
└── teste/TestesRegrasNegocio  testes das regras (Java puro, sem framework)
```

A estrutura real dos arquivos/classes deve ser inspecionada antes de
qualquer alteração. Este documento não deve ser tratado como substituto
da leitura do código.

------------------------------------------------------------------------

## 5. O que já existe

### 5.1 Implementado e testado

-   Login de ADMIN, das 3 lojas e da FÁBRICA (`SistemaShowroom.autenticar`).
-   Peso comercial (soma do peso base das unidades) separado do peso de
    produção.
-   Venda de 1 ou 2 unidades, com modelos e teores iguais ou diferentes.
-   Valor base por unidade: peso comercial × cotação do teor da unidade.
-   Cotação cadastrada pelo ADMIN e congelada em cada venda.
-   Valor: base → acréscimo % → acréscimo R$ → desconto % → final.
-   Peso de produção por unidade: alteração % da venda → P.E. → índice do
    aro → arredondamento para cima em 0,05 g; depois soma das unidades.
-   Aro decimal usa o índice superior; aro < 8 usa 8; aro > 40 usa 40.
-   P.E. por unidade, exibido numa área única (ex.: "F 4,5 mm / M 5,5 mm").
-   "Mais anatômica" por unidade, exibida nas vias (sem efeito de cálculo,
    ver 5.3).
-   Status: PEDIDO CRIADO → OK (ao imprimir na fábrica) → FINALIZADO (com
    peso final opcional) → ENTREGUE; CANCELADO a qualquer momento, sem
    apagar o registro.
-   Edição só antes de FINALIZADO, com a marca `*Editado*` na fábrica.
-   Clientes separados por loja; ADMIN vê todos.
-   Faturamento total, por loja e da própria loja, sem os cancelados.
-   Via do cliente e via da fábrica, sem fórmulas, índices ou percentuais.
-   Entradas numéricas inválidas não derrubam o programa.

### 5.2 Decisões de implementação (não são regras de negócio novas)

-   `CotacaoOuro` passou a ser imutável (os setters, que não eram usados,
    foram removidos). Uma nova cotação cria um novo objeto, e os pedidos
    antigos continuam apontando para o objeto antigo.
-   O arredondamento usa `BigDecimal` para evitar erros de `double`
    (ex.: 1,50 × 0,8 = 1.2000000000000002, que com `Math.ceil` puro
    subiria para 1,25).
-   A regra de P.E. do par foi adaptada para a unidade:
    `(par ÷ largura × desejada) ÷ 2 = unidade ÷ largura × desejada`.
-   Transições de status seguem a ordem do fluxo: só finaliza pedido OK;
    só marca ENTREGUE pedido FINALIZADO; imprimir não altera pedido
    CANCELADO; pedido CANCELADO não pode ser editado.
-   A marca `*Editado*` vale para alterações feitas depois que o pedido
    foi lançado e não é removida depois.
-   `PedidoVenda.exibirVenda()` e `Alianca.exibirAlianca()` foram
    substituídos pelo pacote `impressao` (o domínio não imprime na tela).
-   Os valores do pedido são calculados sob demanda (não ficam guardados
    em campos), para nunca ficarem desatualizados após uma edição.

### 5.3 Pendências conhecidas

-   **Mais anatômica:** falta definir o percentual, quem o configura e em
    que ponto ele entra no valor e no peso de produção. Hoje a
    característica é registrada e impressa, mas **não altera** valor nem
    peso. O menu avisa isso ao marcar a opção.
-   Edição de pedido permite alterar os dados da venda, as unidades,
    adicionar a 2ª unidade, a alteração de peso, os acréscimos e o
    desconto. Não permite trocar o cliente do pedido nem remover uma
    unidade.
-   `Modelo` não possui o campo "especificações" (REGRAS, seção 3).
-   Os campos `valorBase10k`/`valorBase18k` de `Modelo` nunca recebem
    valor (mantidos, sem uso).
-   Datas ainda são `String`; os dados ficam só em memória (perdidos ao
    fechar o programa).

### 5.4 Como compilar e testar

No IntelliJ, `test/` está marcada como pasta de testes: rode o `main` de
`teste.TestesRegrasNegocio`. Pelo terminal (a partir da raiz):

``` text
javac -encoding UTF-8 -d out/teste-build $(find src test -name "*.java")
java -cp out/teste-build teste.TestesRegrasNegocio
```

O programa termina com código 1 se algum teste falhar.

------------------------------------------------------------------------

## 6. Credenciais/dados de teste

O `Main` atual contém objetos de teste com dados de login/senha
(admin, caxias, novohamburgo, portoalegre e fabrica).

Esses dados são apenas dados de desenvolvimento/teste.

**Não transformar credenciais de teste em credenciais reais nem expô-las
em funcionalidades de produção.**

Ao refatorar o sistema, verificar se essas credenciais devem permanecer
hardcoded apenas para testes ou se devem ser substituídas por outra
estratégia.

------------------------------------------------------------------------

## 7. Objetivo do próximo trabalho do agente

Antes de implementar novas funcionalidades, o agente deve:

1.  Ler este arquivo.
2. Ler `REGRAS_NEGOCIO.md`.
3.  Analisar toda a estrutura atual do projeto.
4.  Ler as classes existentes relevantes.
5.  Identificar o que já está implementado.
6.  Identificar o que está parcialmente implementado.
7.  Identificar o que ainda falta.
8.  Identificar possíveis conflitos entre o código atual e as regras de
    negócio.
9.  Apresentar um diagnóstico antes de fazer alterações.

**Na primeira análise, não alterar nenhum arquivo.**

------------------------------------------------------------------------

## 8. Objetivo de desenvolvimento

O objetivo não é reescrever o projeto do zero.

O agente deve:

-   preservar o trabalho existente;
-   evoluir a implementação atual;
-   melhorar a organização quando necessário;
-   evitar refatorações gigantes sem necessidade;
-   manter a aplicação executável durante o desenvolvimento;
-   respeitar Java 17;
- aplicar as regras de `REGRAS_NEGOCIO.md`.

------------------------------------------------------------------------

## 9. Regras para o agente durante o desenvolvimento

### Antes de alterar

Sempre:

-   entender o código existente;
-   localizar onde a funcionalidade deve ser implementada;
-   verificar se já existe uma implementação relacionada;
-   considerar as regras de negócio.

### Ao alterar

Preferir:

-   mudanças pequenas e incrementais;
-   métodos com responsabilidade clara;
-   classes com responsabilidade clara;
-   reaproveitamento de código existente;
-   nomes em português quando o domínio do projeto estiver em português.

Evitar:

-   reescrever arquivos inteiros sem necessidade;
-   criar duplicação;
-   mover classes/pacotes sem justificativa;
-   introduzir frameworks desnecessariamente;
-   alterar a versão do Java;
-   apagar código existente sem explicar o motivo.

### Ao terminar uma alteração

O agente deve:

-   explicar o que mudou;
-   indicar quais arquivos foram alterados;
-   explicar as regras de negócio envolvidas;
-   executar/indicar testes adequados;
-   informar qualquer problema ou dúvida restante.

------------------------------------------------------------------------

## 10. Relação com o aprendizado

Este projeto também está sendo usado como projeto de estudo de Java.

Portanto, quando uma implementação exigir um conceito importante de
Java, o agente deve **explicar o conceito antes ou junto da
implementação**, em vez de simplesmente esconder a complexidade.

Exemplos:

-   classes e objetos;
-   encapsulamento;
-   métodos;
-   listas/Collections;
-   interfaces;
-   enums;
-   exceções;
-   organização em pacotes;
-   separação de responsabilidades.

Quando houver mais de uma solução possível, apresentar brevemente as
opções e justificar a escolhida.

------------------------------------------------------------------------

## 11. O que NÃO fazer sem confirmação

Não fazer sem confirmação explícita:

-   migrar para Spring Boot;
-   adicionar banco de dados;
-   adicionar Docker;
-   criar API REST;
-   mudar para Java 21/25;
-   reescrever a arquitetura;
-   adicionar frameworks grandes;
-   alterar regras de cálculo;
-   remover funcionalidades existentes;
-   apagar classes/pacotes.

Essas evoluções podem acontecer futuramente, mas não devem ser
introduzidas apenas por iniciativa do agente.

------------------------------------------------------------------------

## 12. Primeiro pedido recomendado ao agente

Depois de ler os dois arquivos, a primeira instrução recomendada é:

> Leia `CONTEXTO_PROJETO.md` e `REGRAS_NEGOCIO.md`.
>
> Analise todo o projeto atual antes de fazer qualquer alteração.
>
> Quero que você compare a implementação existente com as regras de
> negócio documentadas.
>
> Identifique: 1. o que já está implementado; 2. o que está parcialmente
> implementado; 3. o que está faltando; 4. possíveis problemas de
> arquitetura/organização; 5. possíveis divergências entre o código e as
> regras de negócio.
>
> Não altere nenhum arquivo nesta primeira análise. No final, apresente
> um plano de próximos passos em ordem de prioridade.
