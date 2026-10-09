# AppMyNotes

Aplicativo Android para gerenciamento de pequenas anotações, desenvolvido em Java com SQLite e Navigation Drawer.

## Funcionalidades

- CRUD completo de anotações e prioridades.
- Anotações com título, texto e prioridade.
- Prioridades com descrição e cor hexadecimal (`#RRGGBB`).
- Listas personalizadas com destaque visual pela cor da prioridade.
- Toque curto para editar e toque longo para excluir.
- Proteção contra exclusão de prioridades associadas a anotações.
- Ordenação de anotações por prioridade ou por título.
- Persistência local utilizando as tabelas `anotacao_tab` e `prioridade_tab`.

## Como executar

1. Abra a pasta raiz do projeto no Android Studio.
2. Aguarde a sincronização do Gradle.
3. Selecione um emulador ou dispositivo Android.
4. Execute a configuração `app`.

O projeto utiliza Android SDK 37, Java 11 e Gradle Wrapper.

## Testes

Para executar os testes locais e o lint:

```powershell
.\gradlew.bat testDebugUnitTest lintDebug
```

Com um dispositivo ou emulador conectado, execute os testes instrumentados:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```
