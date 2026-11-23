-- =====================================================================
-- InovaTech - Script de criação do banco de dados (PostgreSQL)
-- =====================================================================

-- =====================================================================
-- 1. Limpeza do Banco
-- =====================================================================
DROP TABLE IF EXISTS pagamento, solicitacao_edicao, item_pedido, pedido,
                     prato_ingrediente, ingrediente, prato, usuario CASCADE;

-- =====================================================================
-- 2. TABELAS
-- =====================================================================

CREATE TABLE usuario (
    id_usuario INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf CHAR(11) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    login_usuario VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,  -- texto puro
    perfil VARCHAR(20) NOT NULL CHECK (
        perfil IN (
            'ADMINISTRADOR',
            'GARCOM',
            'CHEF_COZINHA',
            'GESTOR_PEDIDOS',
            'CAIXA'
        )
    ),
    trocar_senha BOOLEAN NOT NULL DEFAULT TRUE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_usuario_cpf_formato
        CHECK (cpf ~ '^[0-9]{11}$'),

    CONSTRAINT chk_usuario_email_formato
        CHECK (email ~* '^[^@[:space:]]+@[^@[:space:]]+\.[^@[:space:]]+$')
);


CREATE TABLE prato (
    id_prato INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(500),
    preco NUMERIC(10,2) NOT NULL CHECK (preco > 0),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW()
);


CREATE TABLE ingrediente (
    id_ingrediente INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    unidade_medida VARCHAR(10) NOT NULL CHECK (
        unidade_medida IN ('KG', 'G', 'L', 'ML', 'UN')
    ),
    quantidade_estoque NUMERIC(10,3) NOT NULL DEFAULT 0 CHECK (
        quantidade_estoque >= 0
    ),
    atualizado_em TIMESTAMP NOT NULL DEFAULT NOW()
);


CREATE TABLE prato_ingrediente (
    id_prato INT NOT NULL,
    id_ingrediente INT NOT NULL,
    quantidade NUMERIC(10,3) NOT NULL CHECK (quantidade > 0),

    PRIMARY KEY (id_prato, id_ingrediente),

    FOREIGN KEY (id_prato)
        REFERENCES prato(id_prato)
        ON DELETE CASCADE,

    FOREIGN KEY (id_ingrediente)
        REFERENCES ingrediente(id_ingrediente)
        ON DELETE RESTRICT
);


CREATE TABLE pedido (
    id_pedido INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    id_garcom INT NOT NULL,
    numero_mesa INT,
    nome_comanda VARCHAR(100),

    status VARCHAR(25) NOT NULL DEFAULT 'AGUARDANDO_CONFIRMACAO'
        CHECK (
            status IN (
                'AGUARDANDO_CONFIRMACAO',
                'EM_PREPARO',
                'PRONTO',
                'ENTREGUE',
                'ENCERRADO'
            )
        ),

    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    confirmado_em TIMESTAMP,
    entregue_em TIMESTAMP,
    encerrado_em TIMESTAMP,

    id_ultimo_editor INT,
    editado_em TIMESTAMP,
    id_chef INT,

    CONSTRAINT fk_pedido_garcom
        FOREIGN KEY (id_garcom)
        REFERENCES usuario(id_usuario),

    CONSTRAINT fk_pedido_ultimo_editor
        FOREIGN KEY (id_ultimo_editor)
        REFERENCES usuario(id_usuario),

    CONSTRAINT fk_pedido_chef
        FOREIGN KEY (id_chef)
        REFERENCES usuario(id_usuario),

    CONSTRAINT chk_pedido_numero_mesa
        CHECK (numero_mesa > 0),

    CONSTRAINT chk_pedido_mesa_comanda
        CHECK (
            numero_mesa IS NOT NULL
            OR nome_comanda IS NOT NULL
        ),

    -- as datas devem seguir a ordem natural do fluxo do pedido
    CONSTRAINT chk_pedido_ordem_datas
        CHECK (
            (confirmado_em IS NULL OR confirmado_em >= criado_em)
            AND (entregue_em  IS NULL OR confirmado_em IS NULL OR entregue_em  >= confirmado_em)
            AND (encerrado_em IS NULL OR entregue_em   IS NULL OR encerrado_em >= entregue_em)
        )
);


CREATE TABLE item_pedido (
    id_item INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    id_pedido INT NOT NULL,
    id_prato INT NOT NULL,

    quantidade INT NOT NULL CHECK (quantidade > 0),

    valor_pedido NUMERIC(10,2) NOT NULL CHECK (valor_pedido > 0),

    observacao VARCHAR(255),

    id_chef INT,

    status VARCHAR(15) NOT NULL DEFAULT 'NA_FILA'
        CHECK (
            status IN (
                'NA_FILA',
                'EM_PREPARO',
                'PRONTO'
            )
        ),

    CONSTRAINT fk_item_pedido_pedido
        FOREIGN KEY (id_pedido)
        REFERENCES pedido(id_pedido)
        ON DELETE CASCADE,

    CONSTRAINT fk_item_pedido_prato
        FOREIGN KEY (id_prato)
        REFERENCES prato(id_prato),

    CONSTRAINT fk_item_pedido_chef
        FOREIGN KEY (id_chef)
        REFERENCES usuario(id_usuario)
);


CREATE TABLE solicitacao_edicao (
    id_pedido INT NOT NULL,
    id_garcom INT NOT NULL,
    id_gestor INT NOT NULL,

    descricao VARCHAR(500) NOT NULL,

    status VARCHAR(10) NOT NULL DEFAULT 'PENDENTE'
        CHECK (
            status IN (
                'PENDENTE',
                'ACEITA',
                'RECUSADA'
            )
        ),

    criada_em TIMESTAMP NOT NULL DEFAULT NOW(),
    respondida_em TIMESTAMP,

    CONSTRAINT pk_solicitacao_edicao
        PRIMARY KEY (id_pedido, id_garcom, id_gestor),

    CONSTRAINT fk_solicitacao_edicao_pedido
        FOREIGN KEY (id_pedido)
        REFERENCES pedido(id_pedido)
        ON DELETE CASCADE,

    CONSTRAINT fk_solicitacao_edicao_garcom
        FOREIGN KEY (id_garcom)
        REFERENCES usuario(id_usuario),

    CONSTRAINT fk_solicitacao_edicao_gestor
        FOREIGN KEY (id_gestor)
        REFERENCES usuario(id_usuario),

    -- solicitação pendente não tem data de resposta; as demais precisam ter
    CONSTRAINT chk_solicitacao_resposta
        CHECK (
            (status = 'PENDENTE' AND respondida_em IS NULL)
            OR (status <> 'PENDENTE' AND respondida_em IS NOT NULL)
        )
);


CREATE TABLE pagamento (
    id_pagamento INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    id_pedido INT NOT NULL UNIQUE,
    id_caixa INT NOT NULL,

    valor NUMERIC(10,2) NOT NULL CHECK (valor > 0),

    forma_pagamento VARCHAR(20) NOT NULL
        CHECK (
            forma_pagamento IN (
                'DINHEIRO',
                'PIX',
                'CARTAO_DEBITO',
                'CARTAO_CREDITO'
            )
        ),

    pago_em TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_pagamento_pedido
        FOREIGN KEY (id_pedido)
        REFERENCES pedido(id_pedido),

    CONSTRAINT fk_pagamento_caixa
        FOREIGN KEY (id_caixa)
        REFERENCES usuario(id_usuario)
);

-- =====================================================================
-- 3. Dados Iniciais
-- =====================================================================

-- Usuário administrador inicial.
--   login: admin    senha: 123456789
-- O CPF e o e-mail abaixo são provisórios e devem ser atualizados.
INSERT INTO usuario (nome, cpf, email, login_usuario, senha, perfil, trocar_senha, ativo)
VALUES (
    'Administrador',
    '00000000000',
    'admin@inovatech.com',
    'admin',
    '123456789',
    'ADMINISTRADOR',
    TRUE,
    TRUE
)
ON CONFLICT (login_usuario) DO NOTHING;
