# Исправление ошибок сборки в GitHub Actions

При анализе конфигурации я обнаружил несколько потенциальных причин сбоя сборки в GitHub Actions, связанных с несовпадением версий Java и настроек Gradle.

## Что я сделаю для исправления:

1. **Обновление версий Java**:
   - В файлах [build.gradle.kts](file:///c:/Users/artemkopik/Desktop/Новая%20папка/app/build.gradle.kts) и [android_release.yml](file:///c:/Users/artemkopik/Desktop/Новая%20папка/.github/workflows/android_release.yml) я установлю единую версию **Java 17**. Современные версии Android Gradle Plugin (8.2.2+) требуют именно Java 17 для корректной работы.

2. **Оптимизация [android_release.yml](file:///c:/Users/artemkopik/Desktop/Новая%20папка/.github/workflows/android_release.yml)**:
   - Добавлю автоматическую установку прав на выполнение для Gradle (даже если мы используем системный Gradle).
   - Уточню версию Gradle до **8.5**, которая гарантированно совместима с Android Gradle Plugin 8.2.2.

3. **Исправление [build.gradle.kts](file:///c:/Users/artemkopik/Desktop/Новая%20папка/app/build.gradle.kts)**:
   - Объединю блоки `defaultConfig`, чтобы избежать конфликтов при сборке.
   - Убежусь, что `jvmTarget` установлен в "17".

## Результат:
После внесения этих изменений я отправлю их в репозиторий. GitHub Actions перезапустит сборку, и она должна завершиться успешно, создав рабочий APK.

**Приступаем к исправлению ошибок сборки?**