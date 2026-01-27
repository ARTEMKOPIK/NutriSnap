# Окончательное исправление линтера и усиление автоматизации

Несмотря на прошлые правки, линтер все еще жалуется на названия функций Compose (они должны быть с большой буквы, а линтер по умолчанию хочет с маленькой). Я исправлю это окончательно.

## План действий:

1. **Исправление линтера (PascalCase)**:
   - Обновлю [.editorconfig](file:///c:/Users/artemkopik/Desktop/Новая%20папка/.editorconfig), добавив `root = true` и расширенные правила для Kotlin.
   - В файлах [CameraScreen.kt](file:///c:/Users/artemkopik/Desktop/Новая%20папка/app/src/main/java/com/nutrisnap/ui/main/CameraScreen.kt), [MainScreen.kt](file:///c:/Users/artemkopik/Desktop/Новая%20папка/app/src/main/java/com/nutrisnap/ui/main/MainScreen.kt) и [OnboardingScreen.kt](file:///c:/Users/artemkopik/Desktop/Новая%20папка/app/src/main/java/com/nutrisnap/ui/onboarding/OnboardingScreen.kt) я добавлю аннотацию `@Suppress("FunctionName")` перед функциями-компонентами. Это гарантированный способ заставить линтер игнорировать именование Compose-функций.

2. **Новый уровень автоматизации — Авто-Версионирование**:
   - Я настрою [build.gradle.kts](file:///c:/Users/artemkopik/Desktop/Новая%20папка/app/build.gradle.kts) так, чтобы `versionCode` и `versionName` автоматически генерировались на основе номера сборки GitHub.
   - Тебе больше никогда не придется вручную менять версию приложения перед выпуском — каждая новая сборка будет иметь уникальный номер.

3. **Синхронизация**:
   - Отправлю изменения на GitHub. Теперь `spotlessCheck` точно пройдет, и релиз будет создан с правильной версией.

---

**Применяем финальные исправления и авто-версионирование?**