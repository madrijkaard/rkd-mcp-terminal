# 🖥️ Terminal Compose

**Terminal Compose** é uma aplicação desktop interativa desenvolvida com **Kotlin** e **Jetpack Compose for Desktop**, que simula um terminal visual moderno com suporte a múltiplas abas, navegação por diretórios, visualização e edição de arquivos (incluindo PDFs), comandos customizados e interface gráfica intuitiva.

![screenshot](https://github.com/seu-usuario/seu-repo/assets/demo.png) <!-- Substitua com um link real do print -->

---

## ✨ Funcionalidades principais

- ✅ Interface gráfica moderna em Compose Desktop
- ✅ Múltiplas sessões com abas
- ✅ Comandos intuitivos e responsivos
- ✅ Edição de arquivos `.txt`, `.java`, `.md`, etc.
- ✅ Visualização de arquivos `.pdf`, com detecção de imagens (`[image]`)
- ✅ Scroll por grandes arquivos (`spy n`, `spy b`)
- ✅ Menu com subcomponentes e popup de configurações
- ✅ Arquitetura modular e extensível com `Decorators`

---

## 🚀 Tecnologias utilizadas

| Tecnologia                 | Descrição                                                    |
|---------------------------|--------------------------------------------------------------|
| **Kotlin JVM**            | Linguagem principal                                           |
| **Jetpack Compose Desktop** | UI reativa e moderna para desktop                           |
| **Swing + ComposePanel**  | Integração com a janela principal (JFrame)                   |
| **Apache PDFBox**         | Extração de texto e detecção de imagens em arquivos PDF      |
| **Gradle Kotlin DSL**     | Sistema de build                                             |

---

## 🛠️ Comandos disponíveis

Você interage com o terminal digitando comandos no campo inferior da tela.  
Aqui estão os principais comandos já suportados:

### 📁 Navegação e estrutura

| Comando               | Ação                                                                 |
|----------------------|----------------------------------------------------------------------|
| `ls`                 | Lista os arquivos e pastas do diretório atual                        |
| `cd <pasta>`         | Entra na pasta com prefixo correspondente                            |
| `cd ..`              | Sobe um nível na hierarquia                                          |
| `home`               | Retorna para o diretório pessoal do sistema                          |

---

### 📄 Visualização de arquivos (modo `spy`)

| Comando                     | Ação                                                             |
|----------------------------|------------------------------------------------------------------|
| `spy <arquivo>`            | Visualiza conteúdo de arquivos `.pdf`, `.txt`, `.json`, etc.     |
| `spy <pasta>`              | Lista os arquivos da pasta visualmente                          |
| `spy n`                    | Avança 100 linhas no conteúdo (`next`)                          |
| `spy b`                    | Volta 100 linhas no conteúdo (`back`)                           |
| `spy close`                | Fecha o modo visualização                                        |

> **Nota**: Se um PDF tiver imagens, elas são exibidas como `[image]` no local correspondente.

---

### 📝 Edição de arquivos (modo `file`)

| Comando                 | Ação                                                   |
|------------------------|--------------------------------------------------------|
| `file <arquivo>`       | Abre um arquivo editável (cria se não existir)         |
| `file save`            | Salva o conteúdo editado no arquivo                    |
| `file close`           | Fecha o editor sem salvar alterações adicionais        |

> **Atenção**: Arquivos `.pdf` são apenas visualizáveis com `spy`, não editáveis com `file`.

---

### 🔄 Outras ações

| Comando         | Ação                                             |
|----------------|--------------------------------------------------|
| `new tab`      | Cria uma nova aba/sessão                         |
| `close tab`    | Fecha a aba atual (se houver mais de uma)        |
| `menu`         | Retorna à tela de menu principal                 |
| `exit`         | Fecha o programa                                 |

---

## 📁 Organização do código

A estrutura do projeto é modular, com responsabilidades bem definidas:

```
├── component/      → Composables principais (Body, Header, Menu, Input)
├── control/        → Gerenciador de estado global (StateControl)
├── decorator/      → Decoradores para montar a hierarquia visual
├── dto/            → Modelos de sessão (SessionDto)
├── util/           → Funções auxiliares (PDF, filesystem)
```

---

## 🧪 Requisitos mínimos

- JDK 17+
- Gradle 8+
- Sistema operacional compatível com Compose Desktop (Windows/Linux/macOS)

---

## ▶️ Como executar

```bash
git clone https://github.com/seu-usuario/seu-repo.git
cd seu-repo
./gradlew run
```

---

## 📸 Capturas de tela

| Modo Terminal com PDF | Múltiplas Abas |
|-----------------------|----------------|
| ![spy-mode](./assets/spy.png) | ![tabs](./assets/tabs.png) |

> **Dica**: Crie a pasta `assets/` e adicione prints reais do seu app.

---

## 📄 Licença

Este projeto é open-source e pode ser modificado livremente.  
Sinta-se à vontade para contribuir com ideias, correções ou melhorias!

---

## 🙌 Autor

Desenvolvido por **[Seu Nome ou Nick]**  
🧠 Projeto pessoal para aprendizado e produtividade

---