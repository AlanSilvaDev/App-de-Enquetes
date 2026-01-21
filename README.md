# 📊 Painel de Votação – App de Enquetes (Android)

Aplicativo **Android nativo**, desenvolvido em **Java**, para criação e gestão de **enquetes em tempo real**, utilizando o **Firebase** como backend.  
O sistema suporta **votação anónima**, **atualização instantânea dos resultados** e uma **área administrativa** para configuração, auditoria e controlo da enquete.


![telas](telas.png)

---

## 🚀 Funcionalidades

### 👥 Para os Votantes
- **Votação em Tempo Real**  
  Escolha entre até **três opções configuráveis**, com resultados atualizados instantaneamente via Firestore.

- **Login Anónimo**  
  Autenticação através do **Firebase Authentication (Anonymous Auth)**, garantindo identidade única sem necessidade de registo manual.

- **Controle de Voto Único**  
  Cada utilizador pode votar **apenas uma vez por enquete**, evitando duplicidade.

- **Informações do Voto**  
  Visualização de metadados do próprio voto:
  - Data e hora  
  - Modelo do dispositivo  
  - Versão do Android  

---

### 🛠️ Para Administradores (Professores)
- **Configuração Dinâmica da Enquete**
  - Título da enquete  
  - Opções de resposta  
  - Mensagem de rodapé  
  - Data e hora limite para encerramento  

- **Gestão de Votos**
  - Reset da votação mediante **código de segurança** (`1234`)

- **Lista de Votantes**
  - Acesso a todos os votos registados  
  - Ordenação por data e hora  

- **Logs de Auditoria**
  - Registo automático de eventos críticos (ex.: reset da enquete)
  - Armazenamento no Firestore

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Java  
- **Plataforma:** Android SDK  
  - Min SDK: 24  
  - Target SDK: 36  

### 🔥 Backend
- **Firebase Firestore**  
  Base de dados NoSQL para enquetes, votos e logs.

- **Firebase Authentication**  
  Autenticação anónima dos utilizadores.

### 🎨 Interface
- Material Design  
- ConstraintLayout  
- CardView  
- Ripple Effects  
- Componentes customizados  

### ⚙️ Build System
- Gradle com **Kotlin DSL**



