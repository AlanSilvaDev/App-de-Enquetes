📊 Painel de Votação - App de Enquetes
Este é um aplicativo Android nativo desenvolvido em Java que permite a criação e gestão de enquetes em tempo real, utilizando o Firebase como backend. O projeto foi estruturado para suportar votações anónimas, monitorização de resultados em tempo real e uma área administrativa para configuração e auditoria.

🚀 Funcionalidades
Para os Votantes:
Votação em Tempo Real: Escolha entre três opções configuráveis e veja os resultados serem atualizados instantaneamente.

Login Anónimo: Integração com Firebase Auth para garantir que cada utilizador tenha uma identidade única sem necessidade de registo manual.

Controle de Voto Único: O sistema impede que o mesmo utilizador vote mais de uma vez na mesma enquete.

Informações do Voto: Visualização de metadados do próprio voto, como a data, o modelo do dispositivo e a versão do Android utilizada.

Para Administradores (Professores):
Configuração Dinâmica: Alteração do título da enquete, das opções de resposta, mensagem de rodapé e definição de uma data/hora limite para o encerramento.

Gestão de Votos: Função para zerar a votação mediante a introdução de um código de segurança ("1234").

Lista de Votantes: Acesso a uma lista detalhada de todos os votos registados, ordenada por data.

Logs de Auditoria: Registo automático de eventos importantes (como o reset da enquete) no Firestore.

🛠️ Tecnologias Utilizadas
Linguagem: Java

Android SDK: Target 36 / Min 24

Backend:

Firebase Firestore: Base de dados NoSQL para armazenamento de enquetes, votos e logs.

Firebase Authentication: Gestão de sessões anónimas.

Interface: Material Design, ConstraintLayout e componentes customizados (Ripple effects, CardViews).

Build System: Gradle com Kotlin DSL.

📂 Estrutura do Projeto
MainActivity.java: Ecrã principal de votação e exibição de resultados.

ConfigurarEnqueteActivity.java: Interface para gestão de conteúdos e prazos da enquete.

ListaVotantesActivity.java: Listagem de auditoria de votos.

EnqueteRepository.java: Camada de abstração para todas as operações de leitura e escrita no Firebase.

FirebaseManager.java: Singleton responsável pela inicialização e acesso aos serviços do Firebase.

Enquete.java: Modelo de dados representativo da estrutura no Firestore.

⚙️ Configuração
Clone o repositório.

Crie um projeto no Firebase Console.

Ative o Firestore Database e o Anonymous Auth.

Adicione o ficheiro google-services.json na pasta app/.

Compile o projeto no Android Studio.
