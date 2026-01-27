# Исправление ошибок компиляции и предупреждений сборки

Я проанализировал логи сборки и обнаружил несколько проблем: отсутствующую библиотеку иконок, неверные ссылки на иконки и предупреждение от базы данных Room.

## Что я сделаю:

1. **Добавление библиотеки иконок**:
   - В [build.gradle.kts](file:///c:/Users/artemkopik/Desktop/Новая%20папка/app/build.gradle.kts) я добавлю зависимость `androidx.compose.material:material-icons-extended`. Большинство иконок, таких как `PhotoLibrary`, находятся именно там.

2. **Исправление [CameraScreen.kt](file:///c:/Users/artemkopik/Desktop/Новая%20папка/app/src/main/java/com/nutrisnap/ui/main/CameraScreen.kt)**:
   - Заменю несуществующую ссылку `Icons.Default.Camera` на стандартную `Icons.Default.PhotoCamera`.

3. **Исправление [MainScreen.kt](file:///c:/Users/artemkopik/Desktop/Новая%20папка/app/src/main/java/com/nutrisnap/ui/main/MainScreen.kt)**:
   - Убежусь, что все импорты иконок корректны после добавления расширенной библиотеки.

4. **Устранение предупреждения Room в [AppDatabase.kt](file:///c:/Users/artemkopik/Desktop/Новая%20папка/app/src/main/java/com/nutrisnap/data/local/AppDatabase.kt)**:
   - Добавлю параметр `exportSchema = false` в аннотацию `@Database`. Это уберет предупреждение о том, что Room некуда экспортировать схему базы данных.

5. **Синхронизация**:
   - Отправлю исправления на GitHub. Сборка в Actions должна пройти успешно и создать APK.

---

**Приступаем к исправлению ошибок компиляции?**