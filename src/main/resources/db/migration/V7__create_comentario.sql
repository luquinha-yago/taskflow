CREATE TABLE public.comentario (
    codigo bigserial NOT NULL,
    comentario text,
    data_hora_registro timestamp,
    codigo_funcionario bigint,
    codigo_tarefa bigint,
    status text DEFAULT 'ATIVO',
    PRIMARY KEY (codigo)
);

ALTER TABLE public.comentario
    ADD CONSTRAINT fk_comentario_funcionario
        FOREIGN KEY (codigo_funcionario)
        REFERENCES public.funcionario (codigo),
    ADD CONSTRAINT fk_comentario_tarefa
        FOREIGN KEY (codigo_tarefa)
        REFERENCES public.tarefa (codigo);