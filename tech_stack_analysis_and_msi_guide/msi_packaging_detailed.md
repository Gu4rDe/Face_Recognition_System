# 📦 MSI Упаковка в Compose Desktop (Face Recognition System)

## 🎯 Обзор

**MSI** (Microsoft Installer) — это формат установщика для Windows, который используется в проекте **Face Recognition System** для создания профессионального инсталлера приложения.

---

## 📚 Что такое MSI?

### **MSI (Microsoft Installer)**
- **Полное имя:** Microsoft Installer
- **Расширение файла:** `.msi`
- **Платформа:** Windows (XP и выше)
- **Тип:** Системный формат установки Windows
- **Особенность:** Встроен в ОС Windows начиная с Windows 2000

### **Альтернативные форматы в Compose Desktop**
| Формат | ОС | Расширение | Инструмент |
|--------|----|------------|-----------|
| **MSI** | Windows | `.msi` | WiX Toolset или jpackage |
| **EXE** | Windows | `.exe` | jpackage (bundled MSI) |
| **DMG** | macOS | `.dmg` | jpackage + hdiutil |
| **DEB** | Linux | `.deb` | jpackage |
| **RPM** | Linux | `.rpm` | jpackage |

---

## 🛠️ Как работает MSI упаковка в Compose

### **Включающие компоненты**

1. **Gradle Plugin** — `org.jetbrains.compose`
   - Версия в проекте: не явно указана (используется из settings.gradle.kts)
   - Автоматически добавляет tasks для упаковки

2. **jpackage** — встроенный инструмент JDK
   - Часть JDK 14+
   - Создаёт native installers для разных OS
   - Bundled JVM включается в инсталлер

3. **WiX Toolset** (на Windows)
   - Используется jpackage internally
   - Создаёт MSI файлы из промежуточного формата

### **Конфигурация в build.gradle.kts**

```kotlin
plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

compose.desktop {
    application {
        mainClass = "com.example.kotlinapp.MainKt"
        
        nativeDistributions {
            // Указываем целевые форматы упаковки
            targetFormats(
                TargetFormat.Msi,    // Windows installer
                TargetFormat.Exe,    // Windows executable
                TargetFormat.Dmg,    // macOS
                TargetFormat.Deb     // Linux
            )
            
            // Метаданные приложения
            packageName = "FaceRecognitionSystem"
            packageVersion = "1.0.0"
            description = "Desktop client for face recognition"
            vendor = "GuArDe"
            
            // Windows-специфичные настройки
            windows {
                // Шорткут на рабочем столе
                shortcut = true
                
                // Меню "Пуск"
                menu = true
                
                // Директория по умолчанию
                installationPath = "C:\\Program Files\\FaceRecognitionSystem"
            }
        }
    }
}
```

---

## 🔧 Процесс создания MSI

### **Этап 1: Подготовка**
```
src/main/kotlin/
  ├── App.kt
  ├── main.kt
  └── ...
```

### **Этап 2: Компиляция и упаковка**
```bash
./gradlew packageMsi
# или для release версии с ProGuard:
./gradlew packageReleaseMsi
```

### **Этап 3: Процесс сборки**
```
1. Компиляция Kotlin кода → .class файлы
2. Создание JAR архива с приложением
3. Сбор зависимостей (все .jar библиотеки)
4. Загрузка необходимых JDK модулей (через jlink)
5. Создание приложения с bundled JVM
6. Генерация MSI через jpackage + WiX
```

### **Этап 4: Выход**
```
build/compose/binaries/main/msi/
  └── FaceRecognitionSystem-1.0.0.msi  ← Готовый инсталлер
```

---

## 📋 Сравнение с другими форматами

### **Windows форматы**

| Параметр | MSI | EXE | Portable EXE |
|----------|-----|-----|--------------|
| **Размер** | 150-250 MB | 150-250 MB | 150-250 MB |
| **Требуется ПО** | встроен в Windows | встроен в Windows | ничего |
| **Установка** | Через Add/Remove Programs | Простой drag-and-drop | Запуск без установки |
| **Реестр Windows** | Да (регистрирует приложение) | Да | Нет |
| **Деинстал** | Control Panel → Remove | Простое удаление файла | Запуск из папки |
| **Проф-изм** | Очень профессионально | Профессионально | Менее профессионально |
| **Автообновление** | Может быть настроено | Может быть настроено | Может быть настроено |

---

## 📊 Структура MSI инсталлера

Внутри `.msi` файла содержится:

```
FaceRecognitionSystem-1.0.0.msi
├── Application Files
│   ├── FaceRecognitionSystem.jar
│   ├── lib/
│   │   ├── ktor-client-core-3.0.3.jar
│   │   ├── compose-runtime-1.10.3.jar
│   │   └── ... (все остальные .jar)
│   └── resources/
│       ├── icons/
│       ├── themes/
│       └── ...
├── JVM Runtime
│   ├── bin/java.exe
│   ├── lib/
│   │   ├── modules (выбранные JDK модули через jlink)
│   │   └── ...
│   └── ... (полная Java Runtime Environment)
├── Shortcuts
│   ├── Desktop shortcut → FaceRecognitionSystem.exe
│   └── Start Menu → Programs/FaceRecognitionSystem
├── Registry entries
│   ├── HKEY_LOCAL_MACHINE\Software\Microsoft\Windows\CurrentVersion\Uninstall
│   └── ... (информация для деинстала)
└── Installation scripts
    ├── Pre-install checks
    └── Registry cleanup (при деинстале)
```

---

## 🚀 Gradle Tasks для упаковки

### **Основные задачи**

```bash
# Упаковка для текущей ОС
./gradlew packageDistributionForCurrentOS

# Windows-специфичные (работают только на Windows)
./gradlew packageMsi          # Основная MSI сборка
./gradlew packageReleaseMsi   # Release с ProGuard обфускацией
./gradlew packageExe          # EXE инсталлер (bundled MSI)

# macOS-специфичные (работают только на macOS)
./gradlew packageDmg          # DMG образ
./gradlew packageReleaseDmg

# Linux-специфичные (работают только на Linux)
./gradlew packageDeb          # Debian пакет
./gradlew packageReleaseDeb

# Запуск приложения
./gradlew run                 # Debug режим
./gradlew runRelease          # Release режим

# Просмотр всех tasks
./gradlew tasks --group "compose desktop"
```

### **Важное ограничение: Cross-compilation**
⚠️ **Нет кроссплатформенной компиляции!**
- Для создания MSI → нужна Windows машина
- Для создания DMG → нужна macOS машина  
- Для создания DEB → нужна Linux машина

---

## 🔐 Отдельные этапы упаковки

### **1. Разработка**
```bash
./gradlew run
# Запускает приложение в debug режиме
# Изменения в коде сразу видны (Hot Reload)
```

### **2. Тестирование Release версии**
```bash
./gradlew runRelease
# Запускает приложение как будто из инсталлера
# ProGuard обфускирует код (может быть медленнее)
```

### **3. Создание инсталлера**
```bash
./gradlew packageMsi
# Создаёт .msi файл в build/compose/binaries/main/msi/
# Размер: 150-250 MB (включает JVM)
```

### **4. Создание инсталлера Release версии**
```bash
./gradlew packageReleaseMsi
# То же самое, но с:
# - ProGuard обфускацией
# - Optimization кода
# - Меньшим размером (немного)
```

---

## 📦 Содержимое инсталлера (Bundled JVM)

### **Почему JVM bundled в инсталлер?**

✅ **Преимущества:**
- Пользователю не нужно устанавливать Java
- Гарантирует совместимость (exact JVM version)
- Standalone приложение
- Профессионально выглядит

❌ **Недостатки:**
- Большой размер (~150+ MB)
- Обновления требуют переустановку
- Место на диске удваивается (exe + данные)

### **Оптимизация размера через jlink**

```kotlin
compose.desktop {
    application {
        // jlink может выбрать только нужные JDK модули
        // Вместо всех 6000+ классов Java
        
        nativeDistributions {
            modules = listOf(
                "java.base",
                "java.desktop",
                "java.logging",
                "java.net.http",
                "java.prefs"
            )
        }
    }
}
```

**Результат:**
- Стандартный JDK: ~300 MB
- После jlink: ~100 MB
- В инсталлере: ~150 MB (+ приложение)

---

## ⚙️ Конфигурация специфично для Windows (MSI)

```kotlin
compose.desktop {
    application {
        mainClass = "com.example.kotlinapp.MainKt"
        
        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "FaceRecognitionSystem"
            packageVersion = "1.0.0"
            description = "Face Recognition Desktop Application"
            
            // Windows-специфичные настройки
            windows {
                // Иконка приложения
                iconFile.set(project.file("src/main/resources/icon.ico"))
                
                // Создавать ли ярлык на рабочем столе
                shortcut = true
                
                // Меню "Пуск"
                menu = true
                
                // Путь установки по умолчанию
                installationPath = "C:\\Program Files\\FaceRecognitionSystem"
                
                // Тип установщика (NSIS или MSI)
                // В нашем случае: MSI
                
                // Переменные окружения для приложения
                // (опционально)
            }
        }
    }
}
```

---

## 🎨 Брендинг и настройка MSI

### **Что можно кастомизировать**

```kotlin
nativeDistributions {
    // Основная информация
    packageName = "FaceRecognitionSystem"
    version = "1.0.0"
    description = "Desktop client for face recognition"
    copyright = "© 2026 GuArDe"
    vendor = "GuArDe"
    
    // Иконки
    windows {
        iconFile.set(file("src/main/resources/icon.ico"))
        // 256x256 или 512x512 рекомендуется
    }
    
    // Переменные окружения
    windows {
        // APP_HOME автоматически устанавливается
        // Путь к папке приложения
    }
}
```

### **Иконка приложения**
```
src/main/resources/
└── icon.ico  ← Windows иконка (256x256 минимум)
```

---

## 🔄 Жизненный цикл MSI инсталлера

### **Установка (User perspective)**
```
1. Скачать FaceRecognitionSystem-1.0.0.msi
2. Двойной клик на файл
3. Windows Installer открывает диалог
4. Пользователь выбирает папку установки
5. Выбор компонентов (опционально)
6. Создание ярлыков (рабочий стол, меню)
7. Копирование файлов
8. Регистрация в реестре Windows
9. Готово! Приложение в меню "Пуск"
```

### **Деинсталяция**
```
1. Control Panel → Add/Remove Programs
2. Найти "FaceRecognitionSystem"
3. Нажать "Uninstall"
4. Windows удаляет файлы
5. Удаляет записи из реестра
6. Удаляет ярлыки
7. Готово!
```

---

## 📊 Размеры и производительность

### **Типичные размеры MSI**

| Компонент | Размер |
|-----------|--------|
| JVM Runtime | ~100 MB |
| Приложение + библиотеки | ~50 MB |
| Ktor Client, Compose, etc. | ~30 MB |
| Resources (icons, assets) | ~5 MB |
| **Итого MSI** | **~150-200 MB** |

### **Процесс установки**
| Операция | Время |
|----------|-------|
| Распаковка архива | 30-60 сек |
| Копирование файлов | 20-40 сек |
| Регистрация в реестре | 5 сек |
| Создание ярлыков | 2 сек |
| **Итого** | **~1-2 минуты** |

### **Первый запуск приложения**
| Этап | Время |
|------|-------|
| Загрузка JVM | 1-2 сек |
| Инициализация Compose | 1-2 сек |
| Загрузка главного экрана | 1-2 сек |
| **Итого** | **3-6 сек** |

---

## 🚨 Потенциальные проблемы и решения

### **Проблема 1: MSI требует Admin права**
```
❌ Проблема: Windows запрашивает admin права при установке
✅ Решение: Нормально для Windows приложений
✅ Решение: Можно отключить через конфигурацию (не рекомендуется)
```

### **Проблема 2: "Cannot create MSI on non-Windows OS"**
```
❌ Проблема: Запуск ./gradlew packageMsi на macOS/Linux
✅ Решение: Использовать Windows машину ИЛИ виртуальную машину
✅ Решение: Использовать CI/CD (GitHub Actions на Windows runner)
```

### **Проблема 3: Размер инсталлера 200+ MB**
```
❌ Проблема: Слишком большой файл для распространения
✅ Решение: Оптимизировать jlink modules (смотри выше)
✅ Решение: Использовать инструмент Conveyor (коммерческий)
✅ Решение: Упаковать EXE вместо MSI (эффективнее)
```

### **Проблема 4: ProGuard несовместимость**
```
❌ Проблема: "Unsupported version number [66.0] (maximum 62.65535)"
✅ Решение: Использовать Java 17 или 18 вместо 21+
✅ Решение: Отключить ProGuard (не обфусцировать)
```

---

## 🎯 Альтернативы MSI

### **1. EXE (Windows Executable)**
```bash
./gradlew packageExe
```
- Это bundled MSI в автоинсталлере EXE
- Более удобен для распространения
- Тот же результат, но проще для юзера

### **2. Conveyor (JetBrains)**
```
Коммерческое решение с:
- Кроссплатформенной сборкой
- Онлайн обновлениями
- Меньшим размером
- Подписанием кода (code signing)
```

### **3. NSIS (Nullsoft Scriptable Install System)**
```
Альтернативный инсталлер для Windows:
- Меньший размер
- Большая кастомизация
- Но требует отдельной настройки
```

---

## 📋 Чек-лист для создания MSI

- [ ] Убедиться что находишься на Windows машине
- [ ] Проверить что JDK 17+ установлена (`java -version`)
- [ ] Убедиться что iconFile.set указывает на существующий файл
- [ ] Правильная версия в `packageVersion`
- [ ] Правильный `mainClass` (точка входа в приложение)
- [ ] Протестировать приложение в режиме `./gradlew runRelease`
- [ ] Запустить `./gradlew packageMsi`
- [ ] Проверить что файл создан в `build/compose/binaries/main/msi/`
- [ ] Скачать инсталлер на тестовую машину
- [ ] Запустить инсталлер
- [ ] Проверить что приложение работает
- [ ] Проверить что ярлыки созданы (рабочий стол, меню)
- [ ] Проверить деинсталляцию через Control Panel
- [ ] Готово к распространению! 🎉

---

## 📚 Дополнительные ресурсы

### **Официальная документация**
- [Kotlin Multiplatform Native Distributions](https://kotlinlang.org/docs/multiplatform/compose-native-distribution.html)
- [Compose Desktop Documentation](https://github.com/JetBrains/compose-multiplatform)

### **Инструменты**
- **jpackage** — встроен в JDK 14+
- **WiX Toolset** — используется jpackage internally
- **Conveyor** — commercial tool от JetBrains

### **Типичный workflow**
```bash
# Разработка
./gradlew run

# Тестирование перед упаковкой
./gradlew runRelease

# Создание инсталлера
./gradlew packageMsi

# Вывод
build/compose/binaries/main/msi/FaceRecognitionSystem-1.0.0.msi
```

---

## 🎓 Заключение

**MSI в Compose Desktop** это:
- ✅ Профессиональный способ распространения на Windows
- ✅ Встроенная интеграция с Windows (реестр, меню, Control Panel)
- ✅ Bundled JVM (пользователь не устанавливает Java)
- ✅ Простой процесс через Gradle tasks
- ❌ Требует Windows машину для сборки
- ❌ Большой размер (~150-200 MB)
- ❌ Нет автоматических обновлений (по умолчанию)

Для **Face Recognition System** это идеальный выбор, так как:
1. Desktop приложение для Windows пользователей
2. Нужна GUI (Material3)
3. Требуется установка зависимостей (лучше bundled JVM)
4. Профессиональный вид важен для корпоративного использования

---

**Дата анализа:** 22.04.2026
