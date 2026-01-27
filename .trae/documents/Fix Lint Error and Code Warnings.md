# Исправление ошибок и предупреждений Lint

Я проанализировал отчет о сборке и подготовил план по устранению критической ошибки Lint и нескольких предупреждений в коде.

## Что я сделаю:

1. **Исправление в [AndroidManifest.xml](file:///c:/Users/artemkopik/Desktop/Новая%20папка/app/src/main/AndroidManifest.xml)**:
   - Добавлю тег `<uses-feature android:name="android.hardware.camera" android:required="false" />`. Это устранит ошибку сборки, сообщающую о том, что использование камеры должно быть явно объявлено как необязательное для совместимости с ChromeOS и устройствами без задней камеры.
   - Также добавлю аналогичный тег для микрофона (`android.hardware.microphone`), так как в приложении есть разрешение на запись аудио.

2. **Очистка кода (Устранение предупреждений)**:
   - **[MainActivity.kt](file:///c:/Users/artemkopik/Desktop/Новая%20папка/app/src/main/java/com/nutrisnap/MainActivity.kt)**: Исправлю небезопасное приведение типов (Unchecked cast) при создании ViewModel.
   - **[MainScreen.kt](file:///c:/Users/artemkopik/Desktop/Новая%20папка/app/src/main/java/com/nutrisnap/ui/main/MainScreen.kt)**: Помечу неиспользуемый параметр `uri` как `_` в лямбда-выражении, чтобы код был чище.
   - **[PdfExporter.kt](file:///c:/Users/artemkopik/Desktop/Новая%20папка/app/src/main/java/com/nutrisnap/util/PdfExporter.kt)**: Удалю неиспользуемую переменную `paint`.

3. **Синхронизация**:
   - Отправлю все исправления на GitHub. После этого сборка в Actions должна пройти успешно, так как мы устранили причину остановки (Lint error).

---

**Приступаем к исправлению ошибок и чистке кода?**