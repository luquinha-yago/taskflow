CREATE TABLE public.projeto (
    codigo bigserial NOT NULL,
    nome text,
    descricao text,
    data_criacao date,
    data_inicio date,
    data_fim date,
    data_conclusao date,
    status_projeto text DEFAULT 'PLANEJADO',
    prioridade text DEFAULT 'MEDIA',
    codigo_departamento bigint,
    status text DEFAULT 'ATIVO',
    PRIMARY KEY (codigo)
);

ALTER TABLE public.projeto
    ADD CONSTRAINT fk_projeto_departamento
        FOREIGN KEY (codigo_departamento)
        REFERENCES public.departamento (codigo);
