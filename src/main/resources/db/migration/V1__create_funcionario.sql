CREATE TABLE public.funcionario (
    codigo bigserial NOT NULL,
    nome text,
    cpf text,
    email text,
    telefone text,
    cargo text,
    data_admissao date,
    status text DEFAULT 'ATIVO',
    PRIMARY KEY (codigo)
);
