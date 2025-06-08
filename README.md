# JWT Validator Application  


**Descrição do Projeto**  
Esta aplicação é um validador de tokens JWT que verifica se um token atende às seguintes regras:  
1. **Estrutura válida de JWT** (assinatura, formato RFC 7519).  
2. **Claims obrigatórias**:  
   - `Name`:  
     - Máximo de **256 caracteres**.  
     - Não pode conter **caracteres numéricos**.  
   - `Role`:  
     - Valores permitidos: `Admin`, `Member` ou `External` (case-sensitive).  
   - `Seed`:  
     - Deve ser um **número primo**.  
3. **Proibido claims adicionais** (apenas `Name`, `Role` e `Seed` são permitidos).  

---

### Stack Tecnológica  
| Categoria         | Tecnologias                          |  
|-------------------|--------------------------------------|  
| **Desenvolvimento** | Java 17, Spring Boot 3               |  
| **Infraestrutura**  | AWS ECS Fargate, Terraform, CloudWatch |  
| **Observabilidade**| Datadog (métricas, logs, monitors)   |  

---

### Execução Local  
#### Pré-requisitos:  
- **Java 17**:  
  - *Windows*: Instale o [JDK 17](https://www.oracle.com/java/technologies/downloads/#java17) e configure `JAVA_HOME`.  
  - *Mac*:  
    ```bash  
    brew install openjdk@17  
    export PATH="/usr/local/opt/openjdk@17/bin:$PATH"  
    ```  
- **Maven** (para build) ou **Docker** (opcional).  

#### Passos:  
1. Clone o repositório:  
   ```bash  
   git clone https://github.com/seu-repositorio/jwt-validator.git  
   cd jwt-validator/app  
   ```  
2. Execute com Maven:  
   ```bash  
   mvn spring-boot:run  
   ```  
3. **OU** use Docker:  
   ```bash  
   docker build -t jwt-validator .  
   docker run -p 8080:8080 jwt-validator  
   ```  
4. Acesse a documentação:  
   [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  

---



### Deploy na AWS (CI/CD)  

#### Repositórios e Fluxo:  
| Repositório       | Função                                  |  
|-------------------|-----------------------------------------|  
| **jwt-config**    | Bucket S3 para estado do Terraform (CI via GitHub Actions). |  
| **jwt-infra**     | Infra AWS (ECS, ALB, CodeDeploy, Datadog) via Terraform. |  
| **jwt-validator** | App + CI/CD (build, testes, push para Docker Hub). |  
| **jwt-datadog-monitors** | Configuração de monitors no Datadog. |  

#### Pipeline CI/CD:  
**Fluxo na branch `develop`:**  
1. Push aciona GitHub Actions  
2. Executa:  
   - Build da aplicação  
   - Testes unitários e integração  
   - Análises de segurança/qualidade  
3. Gera imagem Docker e envia para Docker Hub  

**Fluxo na branch `master`:**  
1. Merge dispara nova action  
2. Realiza deploy na AWS via CodeDeploy  
3. Estratégia canary:  
   - 10% do tráfego inicialmente  
   - Monitoramento por 15 minutos  
   - Escalonamento gradual para 100%  
#### Passos para Deploy:  
1. **Infraestrutura**:  
   - Execute Terraform em `jwt-infra` para criar cluster ECS, ALB e políticas.  
2. **App Deployment**:  
   - Merge na branch `master` dispara a CI/CD do `jwt-validator`:  
     - Gera nova imagem Docker.  
     - Atualiza serviço no ECS via **CodeDeploy** (estratégia canary).  
3. **Monitoramento**:  
   - Monitores do Datadog são configurados via `jwt-datadog-monitors`.  
   - Acompanhe métricas no [Datadog Dashboard](https://app.datadoghq.com).  

---

### Observabilidade  
- **Alertas**:  
  - Alto consumo de CPU/Erros 5xx.  
  - Alterações súbitas em taxa de rejeição.

> **Nota**: Monitores pré-configurados estão em `jwt-datadog-monitors`.  

