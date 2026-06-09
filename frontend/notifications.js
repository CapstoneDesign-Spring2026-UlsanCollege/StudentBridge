(function () {
  const REFRESH_INTERVAL_MS = 60000;
  const controllers = [];

  const fallbackMessages = {
    "notifications.label": "Notifications",
    "notifications.open": "Open notifications",
    "notifications.markAllRead": "Mark all read",
    "notifications.empty": "No notifications yet.",
    "notifications.unread": "unread notifications",
    "notifications.applicationSentTitle": "Application sent",
    "notifications.newApplicationTitle": "New job application",
    "notifications.newMessageTitle": "New message"
  };

  function t(key) {
    if (window.StudentBridgeI18n) {
      return StudentBridgeI18n.t(key);
    }

    return fallbackMessages[key] || key;
  }

  function canReachBackend() {
    return window.StudentBridgePlatform && StudentBridgePlatform.canReachBackend();
  }

  function backendUrl(path) {
    return StudentBridgePlatform.toBackendUrl(path);
  }

  function clientUrl(targetUrl) {
    const value = String(targetUrl || "").trim();

    if (!value) {
      return "";
    }

    if (/^https?:\/\//i.test(value)) {
      return value;
    }

    if (value.startsWith("/")) {
      const base = canReachBackend() ? StudentBridgePlatform.getBackendBase() : "";
      return `${base}${value}`;
    }

    return value;
  }

  function localTitle(notification) {
    const titleKeys = {
      application_sent: "notifications.applicationSentTitle",
      new_application: "notifications.newApplicationTitle",
      new_message: "notifications.newMessageTitle"
    };

    const titleKey = titleKeys[notification.type];
    return titleKey ? t(titleKey) : notification.title;
  }

  async function translateItems(items) {
    if (!window.StudentBridgeI18n || StudentBridgeI18n.getLanguage() === "en") {
      return {};
    }

    return StudentBridgeI18n.translateBatch(items, StudentBridgeI18n.getLanguage());
  }

  function formatDate(value) {
    const cleanValue = String(value || "").trim();

    if (!cleanValue) {
      return "";
    }

    const date = new Date(cleanValue.replace(" ", "T"));

    if (Number.isNaN(date.getTime())) {
      return cleanValue;
    }

    return new Intl.DateTimeFormat(document.documentElement.lang || "en", {
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit"
    }).format(date);
  }

  function setupRoot(root) {
    root.classList.add("notification-root");
    root.innerHTML = "";

    const button = document.createElement("button");
    button.type = "button";
    button.className = "notification-button";
    button.setAttribute("aria-haspopup", "true");
    button.setAttribute("aria-expanded", "false");
    button.setAttribute("aria-label", t("notifications.open"));
    button.innerHTML = '<i class="fa-solid fa-bell" aria-hidden="true"></i>';

    const badge = document.createElement("span");
    badge.className = "notification-badge";
    badge.hidden = true;
    button.appendChild(badge);

    const panel = document.createElement("div");
    panel.className = "notification-panel";
    panel.hidden = true;

    const header = document.createElement("div");
    header.className = "notification-header";

    const heading = document.createElement("strong");
    heading.textContent = t("notifications.label");

    const readAllButton = document.createElement("button");
    readAllButton.type = "button";
    readAllButton.className = "notification-read-all";
    readAllButton.textContent = t("notifications.markAllRead");

    header.append(heading, readAllButton);

    const list = document.createElement("div");
    list.className = "notification-list";

    panel.append(header, list);
    root.append(button, panel);

    const controller = {
      root,
      button,
      badge,
      panel,
      heading,
      readAllButton,
      list,
      notifications: [],
      unreadCount: 0,
      intervalId: null
    };

    button.addEventListener("click", (event) => {
      event.stopPropagation();
      togglePanel(controller);
    });

    readAllButton.addEventListener("click", (event) => {
      event.stopPropagation();
      markAllRead(controller);
    });

    list.addEventListener("click", (event) => {
      const itemButton = event.target.closest("[data-notification-id]");

      if (!itemButton) {
        return;
      }

      openNotification(controller, itemButton.dataset.notificationId);
    });

    controllers.push(controller);
    refreshController(controller);
    controller.intervalId = window.setInterval(
      () => refreshController(controller),
      REFRESH_INTERVAL_MS
    );
  }

  function togglePanel(controller) {
    const shouldOpen = controller.panel.hidden;
    closeAllPanels();
    controller.panel.hidden = !shouldOpen;
    controller.button.setAttribute("aria-expanded", String(shouldOpen));

    if (shouldOpen) {
      refreshController(controller);
    }
  }

  function closeAllPanels() {
    controllers.forEach((controller) => {
      controller.panel.hidden = true;
      controller.button.setAttribute("aria-expanded", "false");
    });
  }

  async function refreshController(controller) {
    if (!canReachBackend()) {
      controller.root.hidden = true;
      return;
    }

    try {
      const response = await fetch(backendUrl("NotificationServlet"), {
        credentials: "include",
        cache: "no-store"
      });

      if (response.status === 401) {
        controller.root.hidden = true;
        return;
      }

      if (!response.ok) {
        throw new Error("Unable to load notifications.");
      }

      const data = await response.json();

      if (!data.loggedIn) {
        controller.root.hidden = true;
        return;
      }

      controller.root.hidden = false;
      controller.notifications = Array.isArray(data.notifications) ? data.notifications : [];
      controller.unreadCount = Number(data.unreadCount || 0);
      render(controller);
    } catch (error) {
      console.error(error);
      controller.root.hidden = true;
    }
  }

  async function render(controller) {
    controller.heading.textContent = t("notifications.label");
    controller.readAllButton.textContent = t("notifications.markAllRead");
    controller.readAllButton.disabled = controller.unreadCount <= 0;

    if (controller.unreadCount > 0) {
      controller.badge.hidden = false;
      controller.badge.textContent = controller.unreadCount > 99 ? "99+" : String(controller.unreadCount);
      controller.button.setAttribute(
        "aria-label",
        `${t("notifications.open")} (${controller.unreadCount} ${t("notifications.unread")})`
      );
    } else {
      controller.badge.hidden = true;
      controller.button.setAttribute("aria-label", t("notifications.open"));
    }

    controller.list.innerHTML = "";

    if (controller.notifications.length === 0) {
      const empty = document.createElement("p");
      empty.className = "notification-empty";
      empty.textContent = t("notifications.empty");
      controller.list.appendChild(empty);
      return;
    }

    const translationItems = [];

    controller.notifications.forEach((notification) => {
      if (!["application_sent", "new_application", "new_message"].includes(notification.type)) {
        translationItems.push({
          key: `title-${notification.id}`,
          text: notification.title || ""
        });
      }

      translationItems.push({
        key: `message-${notification.id}`,
        text: notification.message || ""
      });
    });

    const translated = await translateItems(translationItems);

    controller.notifications.forEach((notification) => {
      const item = document.createElement("button");
      item.type = "button";
      item.className = notification.read ? "notification-item" : "notification-item unread";
      item.dataset.notificationId = notification.id;

      const title = document.createElement("span");
      title.className = "notification-item-title";
      title.textContent = translated[`title-${notification.id}`] || localTitle(notification);

      const message = document.createElement("span");
      message.className = "notification-item-message";
      message.textContent = translated[`message-${notification.id}`] || notification.message || "";

      const time = document.createElement("span");
      time.className = "notification-item-time";
      time.textContent = formatDate(notification.createdAt);

      item.append(title, message, time);
      controller.list.appendChild(item);
    });
  }

  async function markAllRead(controller) {
    if (controller.unreadCount <= 0) {
      return;
    }

    try {
      await postNotificationAction("readAll");
      await refreshController(controller);
    } catch (error) {
      console.error(error);
    }
  }

  async function openNotification(controller, id) {
    const notification = controller.notifications.find((item) => String(item.id) === String(id));

    if (!notification) {
      return;
    }

    try {
      await postNotificationAction("read", { id });
      await refreshController(controller);
    } catch (error) {
      console.error(error);
      return;
    }

    const target = clientUrl(notification.targetUrl);

    if (target) {
      window.location.href = target;
    }
  }

  async function postNotificationAction(action, params = {}) {
    if (!canReachBackend()) {
      return;
    }

    const body = new URLSearchParams();
    body.set("action", action);

    Object.entries(params).forEach(([key, value]) => {
      body.set(key, value);
    });

    const response = await fetch(backendUrl("NotificationServlet"), {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        "X-Requested-With": "XMLHttpRequest"
      },
      body
    });

    if (!response.ok) {
      throw new Error("Unable to update notifications.");
    }
  }

  function refreshAll() {
    controllers.forEach((controller) => refreshController(controller));
  }

  function init() {
    document.querySelectorAll("[data-notification-root]").forEach((root) => {
      setupRoot(root);
    });
  }

  document.addEventListener("click", closeAllPanels);
  window.addEventListener("studentbridge:languagechange", () => {
    controllers.forEach(render);
  });
  window.addEventListener("studentbridge:notifications:refresh", refreshAll);
  window.StudentBridgeNotifications = { refresh: refreshAll };

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
