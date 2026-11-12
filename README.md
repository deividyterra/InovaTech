# InovaTech
Projeto prático desenvolvido para a disciplina de Engenharia de Software (GCC188), do 4º período do curso de Ciência da Computação da Universidade Federal de Lavras (UFLA), referente ao período letivo de 2026.2.

## 1. INTEGRANTES
- Bianca Santos de Assis - 202510351
- Deividy Aparecido Martins Terra - 202420181
- Mateus Nogueira dos Santos - 202510346

## 2. CONTEXTO DO PROBLEMA E SOLUÇÃO
### 2.1 Descrição do problema

O Restaurante e Cantina Padre Bueno enfrenta dificuldades no gerenciamento de pedidos e na organização do processo de preparação dos pratos. Atualmente, os pedidos são registrados manualmente pelos garçons em papéis e encaminhados até a cozinha, onde os chefs selecionam os pedidos e iniciam sua execução.

Esse processo pode ocasionar perda ou esquecimento de pedidos, dificuldade na definição de prioridades, falta de informações sobre o andamento das solicitações e problemas relacionados ao controle do estoque de ingredientes. Também há dificuldades para identificar o responsável pela execução de cada prato e acompanhar quais pedidos já foram preparados e entregues.

Esses problemas podem gerar atrasos, sobrecarga dos funcionários, desperdício ou indisponibilidade de ingredientes, redução da qualidade do serviço e insatisfação dos clientes.

### 2.2 Descrição da solução

O InovaTech irá desenvolver um sistema web para centralizar e organizar o gerenciamento de pedidos e os principais processos relacionados à sua execução no Restaurante e Cantina Padre Bueno.

O sistema permitirá o gerenciamento de usuários, menu, ingredientes e estoque, além do registro e acompanhamento dos pedidos. Cada solicitação poderá ser relacionada à mesa ou comanda do cliente, aos pratos desejados e aos ingredientes necessários para sua preparação.

A aplicação também permitirá acompanhar o fluxo de execução dos pedidos, incluindo a definição do responsável pela preparação, o tempo de espera, o status do prato e sua entrega ao cliente.

O sistema contará com diferentes perfis de acesso para atender às necessidades dos setores do restaurante, incluindo administradores, garçons, chefs de cozinha e responsáveis pelo gerenciamento dos pedidos.

## 3. INSTRUÇÕES PARA USO

Esta seção apresenta as instruções necessárias para que um usuário consiga instalar e executar o sistema InovaTech. As instruções são destinadas a usuários que não possuem conhecimentos de desenvolvimento de software.

### 3.1 Pré-requisitos

Para utilizar o sistema, é necessário possuir:

- Computador com acesso à internet;
- Navegador web atualizado;
- PostgreSQL 18.6 instalado;
- Java JDK 27 instalado;
- Node.js instalado.

### 3.2 Download do projeto

O sistema pode ser obtido por meio do repositório do projeto no GitHub.

Para baixar o projeto:

1. Acesse o repositório do InovaTech;
2. Clique em **Code**;
3. Selecione **Download ZIP**;
4. Extraia o arquivo ZIP em uma pasta de sua preferência.

### 3.3 Configuração do banco de dados

O sistema utiliza o PostgreSQL para armazenar as informações relacionadas aos usuários, pratos, ingredientes, estoque e pedidos.

Após instalar o PostgreSQL:

1. Abra o aplicativo **pgAdmin**;
2. Acesse o servidor PostgreSQL utilizando a senha definida durante a instalação;
3. Crie um novo banco de dados;
4. Defina o nome do banco como `inovatech`;
5. Mantenha a configuração padrão da porta `5432`.

### 3.4 Execução do sistema

Após realizar as instalações e configurações necessárias, execute o sistema conforme as orientações apresentadas na seção **4. INSTRUÇÕES PARA DEVS**.

Após a inicialização, abra um navegador e acesse o endereço disponibilizado pela aplicação.

Por padrão, o sistema poderá ser acessado em:

```text
http://localhost:5173
```
### 3.5 Acesso ao sistema

Após a execução do script `inovatech.sql`, um usuário administrador será criado automaticamente no sistema.

As credenciais iniciais para o primeiro acesso são:

- **Usuário:** `admin`
- **Senha:** `123456789`

Por questões de segurança, a senha padrão deverá ser alterada pelo administrador após o primeiro acesso.

Para realizar o primeiro acesso:

1. Abra o sistema no navegador;
2. Informe o usuário `admin`;
3. Informe a senha `123456789`;
4. Após o login, altere a senha padrão;
5. Acesse a área de gerenciamento de usuários.

A partir do perfil de administrador, será possível **cadastrar novos usuários do sistema**, definindo as informações necessárias e o perfil de acesso correspondente.

Os novos usuários poderão utilizar suas próprias credenciais para acessar o sistema de acordo com as permissões atribuídas ao seu perfil.

## 4. INSTRUÇÕES PARA DEVS

Esta seção apresenta as instruções necessárias para configurar o ambiente de desenvolvimento, obter o código-fonte, instalar as dependências e executar o projeto localmente.

### 4.1 Pré-requisitos

Antes de iniciar o desenvolvimento, é necessário instalar:

- Git;
- Java JDK 27;
- Node.js;
- PostgreSQL 18.6;
- Visual Studio Code ou outra IDE de preferência.

Também é necessário possuir acesso ao repositório do projeto.

### 4.2 Clonando o projeto

O projeto pode ser obtido por meio do Git utilizando o comando:

```bash
git clone https://github.com/deividyterra/InovaTech.git
```

Após o download, entre no diretório do projeto:

```bash
cd InovaTech
```

Também é possível baixar o projeto diretamente pelo GitHub utilizando a opção **Code > Download ZIP**.

### 4.3 Configuração do banco de dados

O sistema utiliza o PostgreSQL para armazenamento dos dados.

Após instalar o PostgreSQL:

1. Abra o PostgreSQL ou o pgAdmin;
2. Crie um banco de dados chamado `inovatech`;
3. Verifique se o PostgreSQL está executando na porta `5432`;
4. Acesse o banco de dados criado;
5. Execute o script `inovatech.sql`, localizado na pasta `backend` do projeto.

O script será responsável por criar as tabelas, relacionamentos e demais estruturas necessárias para o funcionamento do sistema, além de cadastrar o usuário administrador inicial.

A configuração da conexão com o banco deverá conter, inicialmente, informações semelhantes a:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/inovatech
spring.datasource.username=postgres
spring.datasource.password=SUA_SENHA
```

A senha deve ser substituída pela senha definida durante a instalação do PostgreSQL.

### 4.4 Configuração do backend

O backend do sistema é desenvolvido em Java utilizando Spring Boot.

Após acessar a pasta do backend, instale as dependências necessárias utilizando o gerenciador de dependências definido no projeto.

O projeto poderá ser executado com:

```bash
./mvnw spring-boot:run
```

No Windows, pode ser necessário utilizar:

```bash
mvnw.cmd spring-boot:run
```

O backend deverá ser executado, por padrão, na porta:

```text
http://localhost:8080
```

### 4.5 Configuração do frontend

O frontend é desenvolvido utilizando React e JavaScript.

Após acessar a pasta do frontend, instale as dependências do projeto:

```bash
npm install
```

Em seguida, execute a aplicação:

```bash
npm run dev
```

O frontend deverá ser disponibilizado, por padrão, no endereço:

```text
http://localhost:5173
```

### 4.6 Execução do sistema

Para executar o sistema localmente, é necessário manter o banco de dados, o backend e o frontend em execução.

O fluxo de execução é:

1. Iniciar o PostgreSQL;
2. Verificar se o banco de dados `inovatech` está disponível;
3. Iniciar o backend Spring Boot;
4. Iniciar o frontend React;
5. Abrir o navegador;
6. Acessar `http://localhost:5173`.

A aplicação frontend realizará a comunicação com o backend, que será responsável pelo processamento das regras de negócio e pela comunicação com o banco de dados PostgreSQL.

### 4.7 Desenvolvimento e alterações

Durante o desenvolvimento, recomenda-se:

- Criar uma branch específica para cada funcionalidade ou correção;
- Realizar commits com mensagens objetivas;
- Atualizar a branch antes de iniciar novas alterações;
- Testar as alterações localmente antes de enviá-las ao repositório;
- Evitar alterações diretamente na branch principal;
- Manter as dependências e configurações do projeto documentadas.

### 4.8 Estrutura geral do projeto

A estrutura geral dos diretórios do projeto será organizada da seguinte forma:

```text
InovaTech/
├── backend/
│   └── inovatech.sql
├── frontend/
├── README.md
├── docs/
└── .gitignore
```

A pasta `backend` concentra os componentes relacionados à API, às regras de negócio, ao acesso ao banco de dados e ao script de criação do banco.

A pasta `frontend` contém os componentes relacionados à interface e à interação com o usuário.

## 5. Tecnologias Utilizadas
As tecnologias utilizadas no desenvolvimento do projeto estão organizadas de acordo com suas respectivas finalidades:

- **Frontend:** HTML Living Standard, CSS Living Standard, JavaScript ECMAScript 2026, React 19.3.
- **Backend:** Java 27, Spring Boot 4.1.2.
- **Banco de dados:** PostgreSQL 18.6.
- **Outras:** Visual Studio Code 1.145.3 — utilizado como ambiente de desenvolvimento integrado (IDE) para edição, organização e desenvolvimento do código-fonte.
