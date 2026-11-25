# Boas Práticas de Codificação

## 1. Documentação

1. **Descrever a finalidade do programa:** todo programa deve começar com um comentário que apresente, de forma breve e objetiva, sua finalidade e o problema que busca resolver.

## 2. Comentário de Código

1. **Comentar o código:** adicionar comentários que expliquem o objetivo de funções, parâmetros e comportamentos importantes, especialmente quando não forem evidentes pela leitura da implementação. Os comentários devem facilitar a compreensão e a manutenção, sem apenas repetir o que o próprio código já expressa.

2. **Utilizar Javadoc em arquivos Java:** em arquivos `.java`, utilizar o padrão Javadoc para documentar classes e métodos. Os comentários Javadoc começam com `/**` e podem incluir tags como:
   - `@param`: descreve um parâmetro do método;
   - `@return`: descreve o valor retornado;
   - `@throws`: documenta uma exceção que pode ser lançada;
   - `@author`: identifica a autoria, quando aplicável.

   Exemplo:

   ```java
   /**
    * Calcula o valor total de um pedido.
    *
    * @param quantidade quantidade de itens do pedido
    * @param precoUnitario preço de cada item
    * @return valor total do pedido
    */
   public double calcularTotal(int quantidade, double precoUnitario) {
       return quantidade * precoUnitario;
   }
   ```

## 3. Adoção de um Padrão de Notação

O código deve seguir um padrão consistente de organização e nomenclatura, facilitando sua leitura, compreensão e manutenção. Para isso, serão adotadas as seguintes seis práticas:

1. **Indentar o código:** utilizar recuos consistentes para destacar blocos de código, estruturas condicionais, laços de repetição e métodos.

2. **Manter um padrão de indentação:** utilizar a mesma quantidade de espaços nos diferentes níveis de recuo, mantendo a organização visual do código.

3. **Nomear variáveis de maneira intuitiva:** utilizar nomes que representem o conteúdo ou a finalidade das variáveis, evitando nomes genéricos ou sem significado.

4. **Adotar o padrão camelCase:** utilizar nomes iniciados com letra minúscula e escrever cada palavra seguinte com inicial maiúscula, sem espaços ou separadores. Esse padrão será utilizado nos nomes de variáveis e métodos.

   Exemplos:

   ```java
   String nomeUsuario;
   int quantidadePedidos;
   double valorTotal;

   public void calcularValorTotal() {
       // Implementação do método
   }
   ```

5. **Escrever um comando por linha:** organizar cada instrução em uma linha própria, facilitando a leitura e a identificação de erros.

6. **Utilizar chaves nas estruturas de controle:** incluir chaves em estruturas como `if`, `else`, `for` e `while`, mesmo quando a linguagem permitir omiti-las, mantendo a consistência e evitando ambiguidades.
