CREATE TABLE public.tarefa (
    codigo bigserial NOT NULL,
    titulo text,
    descricao text,
    data_criacao date,
    data_inicio date,
    data_prazo date,
    data_conclusao date,
    status_tarefa text DEFAULT 'A_FAZER',
    prioridade text DEFAULT 'MEDIA',
    empresa_id bigint,
    codigo_projeto bigint,
    funcionario_responsavel_id bigint,
    status text DEFAULT 'ATIVO',
    PRIMARY KEY (codigo)
);

ALTER SEQUENCE public.tarefa_codigo_seq OWNED BY public.tarefa.codigo;

ALTER TABLE public.tarefa
    ADD CONSTRAINT fk_tarefa_empresa
        FOREIGN KEY (empresa_id)
        REFERENCES public.empresa (codigo),
    ADD CONSTRAINT fk_tarefa_projeto
        FOREIGN KEY (codigo_projeto)
        REFERENCES public.projeto (codigo),
    ADD CONSTRAINT fk_tarefa_funcionario_responsavel
        FOREIGN KEY (funcionario_responsavel_id)
        REFERENCES public.funcionario (codigo);
