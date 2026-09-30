# REGRAS_NEGOCIO.md

# Sistema Showroom --- Regras de Negócio

> Este documento é a referência das regras de negócio do Sistema
> Showroom. **Não altere uma regra de negócio por suposição.** Se uma
> regra estiver ambígua ou faltando, sinalize antes de implementá-la.

------------------------------------------------------------------------

## 1. Objetivo do sistema

O Sistema Showroom tem como objetivo organizar o fluxo de vendas das
lojas/showrooms e o processamento dos pedidos pela fábrica de joias.

O sistema deverá controlar usuários, lojas, clientes, produtos, vendas,
pedidos e o fluxo de produção/finalização da fábrica.

------------------------------------------------------------------------

## 2. Perfis de acesso

Existem os seguintes locais/perfis principais:

-   ADMIN
-   CAXIAS
-   NOVO HAMBURGO
-   PORTO ALEGRE
-   FÁBRICA

As três lojas (CAXIAS, NOVO HAMBURGO e PORTO ALEGRE) possuem operações
de showroom/loja.

### 2.1 ADMIN

O ADMIN deve poder:

-   visualizar o faturamento total das 3 lojas;
-   visualizar o faturamento específico de cada loja:
    -   somente PORTO ALEGRE;
    -   somente CAXIAS;
    -   somente NOVO HAMBURGO;
-   alterar a cotação do ouro;
-   visualizar/gerenciar clientes cadastrados de **todas** as lojas;
-   visualizar/gerenciar produtos cadastrados;
-   verificar o status dos pedidos enviados para a fábrica.

O ADMIN é a exceção à separação de clientes por loja (seção 2.2.1): ele
possui visão geral.

### 2.2 LOJAS

Cada loja deve poder:

-   visualizar o faturamento da própria loja;
-   cadastrar vendas;
-   cadastrar clientes;
-   alterar informações da venda (respeitando a seção 11);
-   alterar informações do cliente;
-   cancelar/excluir um pedido (seção 11);
-   verificar o status do pedido enviado à fábrica;
-   verificar se o pedido foi finalizado pela fábrica;
-   marcar o pedido como ENTREGUE quando receber as alianças (seção 10);
-   imprimir o pedido para o cliente, respeitando as regras de quais
    informações podem ser exibidas.

#### 2.2.1 Clientes por loja

Os clientes **não são compartilhados** entre as lojas. Cada loja possui
sua própria carteira de clientes.

-   Um usuário de PORTO ALEGRE não pode visualizar clientes de CAXIAS
    nem de NOVO HAMBURGO.
-   Um usuário de CAXIAS não pode visualizar clientes de PORTO ALEGRE
    nem de NOVO HAMBURGO.
-   Um usuário de NOVO HAMBURGO não pode visualizar clientes de CAXIAS
    nem de PORTO ALEGRE.

Exceção: o ADMIN pode visualizar os clientes de todas as lojas.

### 2.3 FÁBRICA

A FÁBRICA deve poder:

-   visualizar/imprimir os pedidos lançados pelas lojas (a impressão
    altera o status para OK --- seção 10);
-   marcar um pedido como FINALIZADO, podendo registrar o peso final
    produzido;
-   consultar o status geral dos pedidos.

------------------------------------------------------------------------

## 3. Cadastro de produtos/modelos

Um produto/modelo cadastrado deve possuir, no mínimo, informações
necessárias para sua identificação e cálculo, incluindo:

-   referência/modelo;
-   largura;
-   especificações;
-   peso do modelo.

No **sistema**, o peso cadastrado no modelo representa o peso base de
**UMA unidade/aliança** (e não do par).

Essa escolha facilita o cálculo das vendas e permite que as duas
unidades de uma venda sejam de modelos diferentes.

O peso base do modelo é considerado para o **aro base 20**.

> Importante: essa representação não deve ser confundida com o processo
> manual atualmente utilizado na fábrica, no qual é comum partir do
> peso do par e dividi-lo por 2 para obter a base individual. No
> sistema, o peso cadastrado **já é** o da unidade e **não deve ser
> dividido por 2**.

------------------------------------------------------------------------

## 4. Unidades da venda e peso comercial

Uma venda pode conter **uma** ou **duas** unidades (alianças).

As duas unidades podem ser do **mesmo modelo** ou de **modelos
diferentes**, e podem ter **teores diferentes** (ex.: unidade F em 10K
e unidade M em 18K).

Cada unidade possui suas próprias características (modelo, teor, aro,
gravação, P.E., mais anatômica).

O **peso comercial da venda** é a soma do peso base das unidades
existentes na venda:

``` text
peso comercial = peso base da unidade 1
               + peso base da unidade 2 (se existir)
```

Exemplos:

``` text
Modelo A: peso base da unidade = 1,50 g
Modelo B: peso base da unidade = 1,80 g

Somente uma unidade (Modelo A):
  peso comercial = 1,50 g

Duas unidades do mesmo modelo (A + A):
  peso comercial = 1,50 + 1,50 = 3,00 g

Duas unidades de modelos diferentes (A + B):
  peso comercial = 1,50 + 1,80 = 3,30 g
```

------------------------------------------------------------------------

## 5. Regra de aro

O aro não é limitado a números inteiros.

São válidos, por exemplo:

``` text
20
20.5
21
21.5
22
```

Valores decimais de aro devem ser aceitos.

Os cálculos de peso relacionados ao aro devem seguir a regra de
arredondamento definida para o sistema: **o resultado deve ser
arredondado para cima** (seção 5.4).

> Importante: não substituir essa regra por arredondamento convencional
> (`round`).

### 5.1 Aro decimal

Aro decimal utiliza o índice do aro **imediatamente superior**.

``` text
20   → índice do aro 20
20.5 → índice do aro 21
21   → índice do aro 21
21.5 → índice do aro 22
```

**Não** calcular um índice intermediário entre dois aros.

### 5.2 Tabela de índices de aro

Os valores da tabela de índices de aro (atualmente em
`calculo/IndiceAro`) são **oficiais**, fornecidos pela fábrica e
utilizados atualmente na produção.

Devem ser mantidos exatamente como estão. **Não** corrigir nem
recalcular os valores com base em uma sequência matemática, mesmo que
algum valor pareça fugir do padrão.

### 5.3 Aro fora da faixa da tabela

A tabela cobre os aros de 8 a 40.

-   Aro **menor que 8** → utilizar o índice do aro 8.
-   Aro **maior que 40** → utilizar o índice do aro 40.

``` text
aro 7  → índice do aro 8
aro 41 → índice do aro 40
```

O sistema **não deve quebrar** nesses casos.

### 5.4 Arredondamento do peso de produção

O peso de produção de **cada aliança** deve ser arredondado **sempre
para cima**, em múltiplos de **0,05 g**.

Se o peso já estiver exatamente em um múltiplo de 0,05 g, ele **não**
sobe para o próximo múltiplo.

``` text
2,01  → 2,05
2,03  → 2,05
2,049 → 2,05
2,05  → 2,05
2,051 → 2,10
2,07  → 2,10
2,11  → 2,15
```

Lógica matemática equivalente:

``` text
Math.ceil(peso / 0.05) * 0.05
```

**Não** utilizar `Math.round()` para essa regra.

------------------------------------------------------------------------

## 6. Regra de largura (P.E.)

### 6.1 P.E. (alteração de largura)

**P.E.** representa uma alteração da largura original do modelo.

O P.E. pode **aumentar ou diminuir** a largura original.

``` text
modelo com largura original de 5,0 mm
  → pode existir P.E. 4,5 mm
  → pode existir P.E. 5,5 mm
```

O peso de referência para produção é recalculado proporcionalmente pela
largura:

``` text
peso ajustado = peso ÷ largura original do modelo × largura desejada
```

Exemplo (processo manual da fábrica, a partir do peso do par):

``` text
peso do par      = 3,00 g
largura original = 5,00 mm
P.E.             = 4,50 mm

3,00 ÷ 5,00 × 4,50 = 2,70 g
```

O pedido possui **uma única área de P.E.**, mas dentro dela pode ser
especificada uma largura diferente para cada unidade:

``` text
P.E.: F 4,5 mm / M 5,5 mm
```

Quando F e M possuem larguras diferentes, o cálculo de produção de cada
unidade usa a largura correspondente àquela unidade.

O vendedor **não** faz esse cálculo. Ele informa no sistema apenas a
característica desejada (por exemplo, "P.E. 4,5 mm"), e o sistema
utiliza a largura original cadastrada no modelo para calcular
internamente o peso necessário para produção.

O pedido da fábrica **não** precisa mostrar a fórmula matemática nem a
porcentagem utilizada.

### 6.2 Cálculo do peso de produção (ordem de aplicação)

O peso de produção é calculado **para cada unidade**, partindo do peso
base da unidade cadastrado no seu modelo (seção 3):

1.  Começa com o **peso base da unidade** (do modelo daquela unidade).
2.  Se houver **alteração percentual de peso**, ela é aplicada no
    início. Essa alteração é um acréscimo **geral da venda** (não de uma
    unidade específica), pode existir mesmo sem nenhuma unidade "mais
    anatômica" e **não** é a mesma coisa que "mais anatômica".
3.  Se houver **P.E.**, o peso é recalculado proporcionalmente pela
    largura: `peso ÷ largura original do modelo × largura desejada`.
4.  Aplica-se o **índice correspondente ao aro** daquela unidade (seções
    5.1, 5.2 e 5.3).
5.  O peso da unidade é **arredondado individualmente para cima** em
    múltiplos de 0,05 g (seção 5.4).

O **peso estimado de produção** da venda é a **soma** dos pesos de
produção das unidades existentes.

> Equivalência com o processo manual: na fábrica, o cálculo costuma
> partir do peso do par, aplicar a alteração percentual e o P.E. e só
> então dividir por 2. Como no sistema o peso cadastrado já é o da
> unidade (seção 3), essa divisão por 2 não é feita. Para um par do
> mesmo modelo, o resultado é o mesmo.

------------------------------------------------------------------------

## 7. Cotação do ouro e valor da venda

A cotação do ouro é controlada pelo ADMIN.

A cotação não deve ser fixa no código.

### 7.1 Peso utilizado no preço da venda

O preço da venda **não** é calculado pelo peso de produção.

O preço parte do **peso comercial** (seção 4), utilizado junto com a
cotação do ouro para formar o **valor base da venda**.

Como as unidades podem ter teores diferentes, a cotação é aplicada
**por unidade**, conforme o teor de cada uma:

``` text
valor base da unidade = peso comercial da unidade × cotação do seu teor

valor base da venda   = valor base da unidade 1
                      + valor base da unidade 2 (se existir)
```

``` text
unidade 1 = 1,50 g
unidade 2 = 1,50 g
peso comercial = 3,00 g

peso estimado de produção (por causa dos aros) = 2,70 g

→ o preço continua baseado no peso comercial de 3,00 g
```

O peso de produção serve para a fabricação e não deve reduzir o valor
vendido.

### 7.2 Cotação congelada na venda

A cotação utilizada em uma venda fica **congelada** naquela venda.

Quando o ADMIN altera a cotação:

-   **novas vendas** utilizam a nova cotação;
-   **vendas antigas** continuam utilizando a cotação vigente no momento
    em que foram registradas.

``` text
Janeiro:   cotação = R$ 500/g → vendas de janeiro ficam com R$ 500/g
Fevereiro: cotação = R$ 550/g → novas vendas usam R$ 550/g
                                vendas antigas continuam com R$ 500/g
```

**Nunca** atualizar automaticamente o valor histórico de uma venda por
causa de uma nova cotação.

### 7.3 Formação do valor da venda (acréscimos e desconto)

O valor da venda é formado nesta ordem:

1.  Valor base da venda
2.  Acréscimo percentual
3.  Acréscimo em valor fixo (R$)
4.  Desconto percentual
5.  Valor final

``` text
valor base
  → acréscimo %
  → acréscimo em R$
  → desconto %
  → valor final
```

Essas regras já possuem elementos no código atual e devem ser
preservadas.

------------------------------------------------------------------------

## 8. Mais anatômica

"Mais anatômica" é uma característica que pode ser informada no pedido,
**por unidade**: somente na F, somente na M ou nas duas.

"Mais anatômica" é **independente** da alteração percentual de peso da
venda (seção 6.2): não confundir as duas.

Ela possui impacto **comercial** e **produtivo**.

Quando uma aliança é marcada como "mais anatômica":

1.  Há um **acréscimo percentual no valor** cobrado do cliente.
2.  Esse acréscimo percentual também **altera o peso** utilizado no
    cálculo de produção.
3.  A porcentagem utilizada internamente **não** aparece no pedido
    enviado para a fábrica.
4.  O pedido da fábrica apenas indica que a peça é "mais anatômica".

A porcentagem exata do acréscimo deve ser tratada como uma regra
**configurável/documentada**. O valor de 10% citado anteriormente foi
apenas um exemplo e **não** deve ser assumido como definitivo sem
confirmação.

------------------------------------------------------------------------

## 9. Pedido de venda e informações exibidas

A loja poderá imprimir um pedido para o cliente.

Essa impressão deve seguir as regras de negócio do sistema e **não deve
simplesmente expor todas as informações internas da fábrica**.

Antes de criar novos campos na impressão, verificar quais informações
pertencem ao cliente e quais são internas.

### 9.1 Conteúdo do pedido de venda

Dados comerciais:

-   loja;
-   vendedor;
-   cliente/CPF;
-   data da venda;
-   data prometida ao cliente;
-   endereço/cidade **do cliente**;
-   valor da venda.

Dados do que foi comprado:

-   modelo;
-   teor de ouro;
-   P.E., se houver;
-   mais anatômica, se houver;
-   aro F + gravação;
-   aro M + gravação.

### 9.2 Pedido da fábrica

A descrição do que foi comprado é praticamente a mesma informação
necessária para a fábrica.

O pedido da fábrica também terá as informações internas necessárias
para a produção, como o resultado do cálculo do peso.

A fábrica **não** precisa receber:

-   a porcentagem utilizada para acréscimos;
-   as fórmulas matemáticas internas.

### 9.3 O que não é exibido ao cliente

Não é necessário mostrar ao cliente:

-   fórmulas matemáticas;
-   índices internos de aro;
-   detalhes internos do cálculo de peso.

------------------------------------------------------------------------

## 10. Status e fluxo do pedido

**Não** existe um status "EM PRODUÇÃO".

O fluxo é:

``` text
PEDIDO CRIADO
  ↓
FÁBRICA IMPRIME
  ↓
OK
  ↓
FÁBRICA PRODUZ
  ↓
FINALIZADO
  ↓
ENTREGUE
```

Regras:

1.  **PEDIDO CRIADO**: a loja registra a venda.
2.  **OK**: quando a fábrica imprime o pedido, o sistema altera
    **automaticamente** o status para OK. Não é necessário um
    funcionário específico para marcar o OK.
3.  **FINALIZADO**: quando a fábrica termina a produção, ela marca o
    pedido como FINALIZADO. Nesse momento a fábrica também poderá
    registrar o peso final produzido.
4.  **ENTREGUE**: quando a loja recebe as alianças, a loja marca o
    pedido como ENTREGUE.

O ADMIN deve conseguir consultar o status geral dos pedidos.

------------------------------------------------------------------------

## 11. Edição e cancelamento do pedido

### 11.1 Edição

Um pedido pode ser **editado** a qualquer momento enquanto **não**
estiver FINALIZADO.

Depois de FINALIZADO, o pedido **não pode mais ser editado**.

Quando um pedido for editado, a fábrica deve visualizar a indicação:

``` text
*Editado*
```

### 11.2 Cancelamento/exclusão

Um pedido pode ser **cancelado/excluído a qualquer momento**, inclusive
depois de FINALIZADO.

O cancelamento **não apaga** o registro: o pedido continua aparecendo no
sistema com status CANCELADO.

Na primeira versão, basta o pedido aparecer visualmente como:

``` text
CANCELADO
```

Melhorias visuais (por exemplo, um card cinza) **não** fazem parte da
primeira versão.

------------------------------------------------------------------------

## 12. Separação entre peso comercial e peso de produção

O projeto deve manter **dois conceitos separados**:

1.  **Peso comercial da venda** (seção 4): utilizado para calcular o
    valor vendido.
2.  **Peso estimado/final de produção** (seção 6.2): calculado conforme
    as características necessárias para fabricação, incluindo, quando
    aplicável:
    -   alteração percentual;
    -   P.E.;
    -   aro de cada unidade;
    -   arredondamento.

Esses dois pesos **não** devem ser tratados como a mesma coisa.

A fábrica também pode registrar o **peso final realmente produzido**.

------------------------------------------------------------------------

## 12.1 Faturamento

O faturamento considera o **valor final** das vendas válidas.

Vendas CANCELADAS continuam aparecendo no sistema, mas **não** são
contabilizadas no faturamento.

-   ADMIN: faturamento total e faturamento de cada loja.
-   LOJA: faturamento da própria loja.

------------------------------------------------------------------------

## 13. Princípios para implementação

Ao implementar as regras:

1.  Não inventar regras de negócio que não estejam documentadas.
2.  Não alterar uma regra existente sem confirmar.
3.  Manter os cálculos de negócio separados da interface sempre que
    possível.
4.  Evitar colocar regras de cálculo diretamente dentro do `main` ou dos
    menus.
5.  Criar métodos/classes responsáveis por cálculos específicos quando
    isso melhorar a organização.
6.  Usar nomes claros e coerentes com o domínio do sistema.
7.  Antes de uma mudança grande, explicar o que será alterado e por quê.
8.  Preservar as regras deste documento mesmo quando houver refatoração.

------------------------------------------------------------------------

## 14. Regras ainda não especificadas

Se uma regra necessária para implementar determinada funcionalidade não
estiver neste documento, o agente deve:

-   identificar a lacuna;
-   explicar por que ela é necessária;
-   pedir confirmação ou propor alternativas;
-   **não escolher arbitrariamente uma regra de negócio**.
