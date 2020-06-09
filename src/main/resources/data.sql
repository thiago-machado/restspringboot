/*
 * Como o banco H2 é um banco que trabalha em memória, ao reiniciar a aplicação, todos os dados 
 * salvos no mesmo serão deletados.
 * 
 * Esse arquivo é uma forma de popular algumas tabelas após a criação das mesmas pelo JPA.
 * 
 * O Spring possui a inteligência de ler esse arquivo após a criação das tabelas. Nenhuma 
 * configuração adicional precisa ser realizada. O arquivo precisa ter o nome data.sql com as 
 * instruções necessárias.
 */
INSERT INTO USUARIO(nome, email, senha) VALUES('Aluno', 'aluno@email.com', '123456');

INSERT INTO CURSO(nome, categoria) VALUES('Spring Boot', 'Programação');
INSERT INTO CURSO(nome, categoria) VALUES('HTML 5', 'Front-end');

INSERT INTO TOPICO(titulo, mensagem, data_criacao, status, autor_id, curso_id) VALUES('Dúvida', 'Erro ao criar projeto', '2019-05-05 18:00:00', 'NAO_RESPONDIDO', 1, 1);
INSERT INTO TOPICO(titulo, mensagem, data_criacao, status, autor_id, curso_id) VALUES('Dúvida 2', 'Projeto não compila', '2019-05-05 19:00:00', 'NAO_RESPONDIDO', 1, 1);
INSERT INTO TOPICO(titulo, mensagem, data_criacao, status, autor_id, curso_id) VALUES('Dúvida 3', 'Tag HTML', '2019-05-05 20:00:00', 'NAO_RESPONDIDO', 1, 2);


INSERT INTO RESPOSTA (data_criacao, mensagem, solucao, autor_id, topico_id) VALUES (CURRENT_TIMESTAMP, 'Primeria resposta', false, 1, 1);
INSERT INTO RESPOSTA (data_criacao, mensagem, solucao, autor_id, topico_id) VALUES (CURRENT_TIMESTAMP, 'Testando outra resposta', false, 1, 1);
