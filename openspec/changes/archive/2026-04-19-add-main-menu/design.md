## Context

KotlinApp — десктопное приложение на Compose Multiplatform (JVM) для администрирования системы распознавания лиц Miit_FaceDetect. Навигация реализована через Voyager (push/pop). После успешного логина пользователь попадает в AdminStubScreen — пустую заглушку с кнопкой «Выйти». Существуют полноценные реализации репозиториев (Employee, FaceRecognition, InviteCode, Settings, Auth), но ни один из них не подключён к UI.

Текущий навигационный поток: HomeScreen -> LoginAdminScreen -> AdminStubScreen. На всех экранах доступен SettingsOverlay.

## Goals / Non-Goals

**Goals:**

- Заменить AdminStubScreen на MainScreen с боковой навигационной панелью (sidebar)
- Обеспечить навигацию между разделами: Dashboard, Сотрудники, Распознавание лиц
- Добавить кнопку Выйти в нижней части sidebar
- Подключить EmployeeRepository и FaceRecognitionRepository к соответствующим экранам
- Отображать агрегированную статистику на Dashboard через EmployeeRepository

**Non-Goals:**

- Управление инвайт-кодами (InviteCodeRepository) — не входит в текущую область
- Управление настройками сервера (SettingsRepository) — оставлено для будущей итерации
- Мобильная адаптация (приложение десктопное)
- Анимации переходов между разделами sidebar

## Decisions

### D1: Sidebar + composable-переключение вместо вложенных Voyager Screen

**Решение:** MainScreen содержит внутреннее состояние (enum MenuItem) и переключает composable-контент внутри себя. Sidebar не использует navigator.push() для разделов — selectedItem определяет какой composable рендерится.

**Альтернатива:** Каждый раздел — отдельный Voyager Screen, sidebar вызывает navigator.push(). Отклонена — sidebar исчезает при переходе, или нужно дублировать его в каждом Screen.

**Обоснование:** Sidebar должен быть постоянно видимым. Composable-переключение проще и не создаёт глубокий стек навигации.

### D2: Material3 NavigationRail

**Решение:** Использовать NavigationRail из Material3 для sidebar.

**Альтернатива:** Кастомный Column с кнопками. Отклонена — NavigationRail даёт стандартные Material3-стили, accessibility, selected-состояния из коробки.

**Обоснование:** NavigationRail — стандартный десктопный паттерн Material3.

### D3: MenuItem как sealed interface

**Решение:** Определить MenuItem как sealed interface с объектами Dashboard, Employees, FaceRecognition. Это даёт exhaustive-when и типобезопасность.

**Обоснование:** Sealed interface + when = compile-time guarantee, легко добавлять новые разделы.

### D4: Dashboard через EmployeeRepository

**Решение:** Dashboard получает статистику (total/active/inactive) через ServiceLocator.employeeRepository.getEmployeeStats() и статус сервера через ServiceLocator.apiService.healthCheck().

**Обоснование:** Данные уже доступны через существующие репозитории.

### D5: Разделы как composable-функции

**Решение:** Содержимое каждого раздела — composable-функция: DashboardContent(), EmployeeContent(), FaceRecognitionContent(). Не отдельные Screen-классы.

**Обоснование:** Согласуется с D1. Если потребуется вложенная навигация — добавить вложенный Navigator.

### D6: Иконки Material Icons

**Решение:** Dashboard -> Icons.Default.Dashboard, Employees -> Icons.Default.People, Face Recognition -> Icons.Default.Face, Logout -> Icons.Default.Logout.

### D7: Выход из аккаунта

**Решение:** Кнопка Выйть в нижней части sidebar вызывает ServiceLocator.authRepository.setToken(null) и navigator.pop() до HomeScreen.

## Risks / Trade-offs

- **[Размер composable]** Composable-контент может стать большим. -> Выносить подкомпоненты в ui/ при росте.
- **[Навигация внутри раздела]** Нет back-stack внутри раздела. -> Добавить вложенный Navigator при необходимости.
- **[Отсутствие InviteCode и Settings в меню]** -> Добавить в следующих итерациях.
- **[BREAKING: AdminStubScreen удалён]** Навигация сломается. -> Замена на MainScreen во всех местах одновременно.