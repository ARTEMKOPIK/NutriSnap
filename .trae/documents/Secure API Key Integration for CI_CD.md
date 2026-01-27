# Настройка безопасного хранения API-ключа для авто-сборки

Так как я не могу зайти в настройки твоего браузера, я не могу нажать кнопку «Добавить секрет» за тебя. Но я могу **подготовить всё в коде**, чтобы после того, как ты вставишь ключ на сайте GitHub, всё заработало само!

## Что я сделаю в коде:

1. **Обновление [build.gradle.kts](file:///c:/Users/artemkopik/Desktop/Новая%20папка/app/build.gradle.kts)**:
   - Настрою проект так, чтобы он умел брать API-ключ из секретов GitHub во время сборки.

2. **Обновление [NutriSnapApp.kt](file:///c:/Users/artemkopik/Desktop/Новая%20папка/app/src/main/java/com/nutrisnap/NutriSnapApp.kt)**:
   - Изменю код, чтобы он не искал ключ в тексте, а брал его из защищенной конфигурации `BuildConfig`.

3. **Обновление [android_release.yml](file:///c:/Users/artemkopik/Desktop/Новая%20папка/.github/workflows/android_release.yml)**:
   - Добавлю команду для GitHub, чтобы он передавал твой секретный ключ в процесс сборки APK.

---

## Что тебе нужно будет сделать (всего 1 минута):

1. Перейди по этой прямой ссылке в свой репозиторий: [https://github.com/ARTEMKOPIK/NutriSnap/settings/secrets/actions/new](https://github.com/ARTEMKOPIK/NutriSnap/settings/secrets/actions/new)
2. В поле **Name** напиши: `GROQ_API_KEY`
3. В поле **Secret** вставь свой ключ (тот самый gsk_... который ты мне давал).
4. Нажми **Add secret**.

**Результат:**
Как только ты это сделаешь и я отправлю обновленный код, GitHub Actions увидит ключ, соберет новый APK, и он будет **полностью рабочим**. Тебе останется только скачать его из раздела **Releases**.

**Приступаем к обновлению кода?**