(function () {
  const REFRESH_INTERVAL_MS = 60000;
  const controllers = [];

  const fallbackMessages = {
    "messages.label": "Messages",
    "messages.open": "Open messages",
    "messages.viewAll": "View all messages",
    "messages.empty": "No messages yet.",
    "messages.unread": "unread messages",
    "messages.noPreview": "No messages yet. Start the conversation.",
    "messages.inboxTitle": "Messages",
    "messages.inboxBody": "Application conversations between students and employers.",
    "messages.chooseThread": "Choose a conversation to start messaging.",
    "messages.writePlaceholder": "Write a short message...",
    "messages.send": "Send",
    "messages.sent": "Message sent.",
    "messages.emptyMessage": "Please write a message first.",
    "messages.loadError": "Messages could not be loaded.",
    "messages.startError": "Conversation could not be opened.",
    "messages.newMessageTitle": "New message",
    "dashboard.applicationsTitle": "Applications",
    "dashboard.applicationsBody": "Review students who applied to your jobs and message them from one place.",
    "dashboard.noApplications": "No applications yet.",
    "dashboard.messageStudent": "Message Student",
    "dashboard.viewCv": "View CV",
    "dashboard.appliedAt": "Applied"
  };

  function t(key, replacements) {
    if (window.StudentBridgeI18n) {
      return StudentBridgeI18n.t(key, replacements);
    }

    let value = fallbackMessages[key] || key;

    Object.entries(replacements || {}).forEach(([name, replacement]) => {
      value = value.replaceAll(`{${name}}`, replacement);
    });

    return value;
  }

  function canReachBackend() {
    return window.StudentBridgePlatform && StudentBridgePlatform.canReachBackend();
  }

  function backendUrl(path) {
    return StudentBridgePlatform.toBackendUrl(path);
  }

  function frontendUrl(path) {
    const cleanPath = String(path || "").replace(/^\/+/, "");

    if (canReachBackend()) {
      const backendBase = StudentBridgePlatform.getBackendBase();

      if (backendBase) {
        return `${backendBase}/frontend/${cleanPath}`;
      }
    }

    if ((window.location.pathname || "").includes("/frontend/")) {
      return `./${cleanPath}`;
    }

    return `./frontend/${cleanPath}`;
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

  function safeExternalUrl(value) {
    const cleanValue = String(value || "").trim();

    if (!cleanValue) {
      return "";
    }

    try {
      const url = new URL(cleanValue, window.location.href);
      return ["http:", "https:"].includes(url.protocol) ? url.href : "";
    } catch (error) {
      return "";
    }
  }

  function translateDisplayText(element, text) {
    const sourceText = String(text || "");
    element.textContent = sourceText;

    if (!window.StudentBridgeI18n || StudentBridgeI18n.getLanguage() === "en" || !sourceText.trim()) {
      return;
    }

    const languageAtRequest = StudentBridgeI18n.getLanguage();

    StudentBridgeI18n.translateText(sourceText, languageAtRequest).then((translatedText) => {
      if (element.isConnected && StudentBridgeI18n.getLanguage() === languageAtRequest) {
        element.textContent = translatedText || sourceText;
      }
    });
  }

  function translateMetaParts(element, parts) {
    const safeParts = parts.filter((part) => part && part.value);
    element.textContent = safeParts.map((part) => part.value).join(" · ");

    if (!window.StudentBridgeI18n || StudentBridgeI18n.getLanguage() === "en") {
      return;
    }

    const languageAtRequest = StudentBridgeI18n.getLanguage();
    const translationItems = safeParts
      .filter((part) => part.translate)
      .map((part, index) => ({
        key: `part${index}`,
        text: part.value
      }));

    if (!translationItems.length) {
      return;
    }

    StudentBridgeI18n.translateBatch(translationItems, languageAtRequest).then((translated) => {
      if (!element.isConnected || StudentBridgeI18n.getLanguage() !== languageAtRequest) {
        return;
      }

      let translatedIndex = 0;
      element.textContent = safeParts.map((part) => {
        if (!part.translate) {
          return part.value;
        }

        const key = `part${translatedIndex}`;
        translatedIndex += 1;
        return translated[key] || part.value;
      }).join(" · ");
    });
  }

  function setupRoot(root) {
    root.classList.add("message-root");
    root.innerHTML = "";

    const button = document.createElement("button");
    button.type = "button";
    button.className = "message-button";
    button.setAttribute("aria-haspopup", "true");
    button.setAttribute("aria-expanded", "false");
    button.setAttribute("aria-label", t("messages.open"));
    button.innerHTML = '<i class="fa-solid fa-message" aria-hidden="true"></i>';

    const badge = document.createElement("span");
    badge.className = "message-badge";
    badge.hidden = true;
    button.appendChild(badge);

    const panel = document.createElement("div");
    panel.className = "message-panel";
    panel.hidden = true;

    const header = document.createElement("div");
    header.className = "message-panel-header";

    const heading = document.createElement("strong");
    heading.textContent = t("messages.label");

    header.appendChild(heading);

    const list = document.createElement("div");
    list.className = "message-list";

    const footer = document.createElement("div");
    footer.className = "message-panel-footer";

    const viewAll = document.createElement("a");
    viewAll.className = "message-view-all";
    viewAll.href = frontendUrl("messages.html");
    viewAll.textContent = t("messages.viewAll");
    footer.appendChild(viewAll);

    panel.append(header, list, footer);
    root.append(button, panel);

    const controller = {
      root,
      button,
      badge,
      panel,
      heading,
      list,
      viewAll,
      conversations: [],
      unreadCount: 0,
      intervalId: null
    };

    button.addEventListener("click", (event) => {
      event.stopPropagation();
      togglePanel(controller);
    });

    list.addEventListener("click", (event) => {
      const itemButton = event.target.closest("[data-message-conversation-id]");

      if (!itemButton) {
        return;
      }

      window.location.href = frontendUrl(
        `messages.html?conversationId=${encodeURIComponent(itemButton.dataset.messageConversationId)}`
      );
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

  async function fetchConversations() {
    const response = await fetch(backendUrl("MessageServlet"), {
      credentials: "include",
      cache: "no-store"
    });

    if (response.status === 401) {
      return { loggedIn: false, unreadCount: 0, conversations: [] };
    }

    if (!response.ok) {
      throw new Error("Unable to load messages.");
    }

    return response.json();
  }

  async function refreshController(controller) {
    if (!canReachBackend()) {
      controller.root.hidden = true;
      return;
    }

    try {
      const data = await fetchConversations();

      if (!data.loggedIn) {
        controller.root.hidden = true;
        return;
      }

      controller.root.hidden = false;
      controller.conversations = Array.isArray(data.conversations) ? data.conversations : [];
      controller.unreadCount = Number(data.unreadCount || 0);
      renderRoot(controller);
    } catch (error) {
      console.error(error);
      controller.root.hidden = true;
    }
  }

  function renderRoot(controller) {
    controller.heading.textContent = t("messages.label");
    controller.viewAll.textContent = t("messages.viewAll");
    controller.viewAll.href = frontendUrl("messages.html");

    if (controller.unreadCount > 0) {
      controller.badge.hidden = false;
      controller.badge.textContent = controller.unreadCount > 99 ? "99+" : String(controller.unreadCount);
      controller.button.setAttribute(
        "aria-label",
        `${t("messages.open")} (${controller.unreadCount} ${t("messages.unread")})`
      );
    } else {
      controller.badge.hidden = true;
      controller.button.setAttribute("aria-label", t("messages.open"));
    }

    renderConversationButtons(controller.list, controller.conversations, null);
  }

  function renderConversationButtons(container, conversations, activeId) {
    container.innerHTML = "";

    if (!conversations.length) {
      const empty = document.createElement("p");
      empty.className = "message-empty";
      empty.textContent = t("messages.empty");
      container.appendChild(empty);
      return;
    }

    conversations.forEach((conversation) => {
      const item = document.createElement("button");
      item.type = "button";
      item.className = conversation.unreadCount > 0 ? "message-item unread" : "message-item";
      item.dataset.messageConversationId = conversation.id;

      if (String(conversation.id) === String(activeId)) {
        item.setAttribute("aria-current", "true");
      }

      const title = document.createElement("span");
      title.className = "message-item-title";
      translateDisplayText(title, conversation.jobTitle || t("messages.label"));

      const preview = document.createElement("span");
      preview.className = "message-item-preview";
      translateDisplayText(preview, conversation.lastMessage || t("messages.noPreview"));

      const meta = document.createElement("span");
      meta.className = "message-item-meta";
      meta.textContent = [
        conversation.otherEmail,
        formatDate(conversation.lastMessageAt)
      ].filter(Boolean).join(" · ");

      item.append(title, preview, meta);
      container.appendChild(item);
    });
  }

  async function postMessageAction(action, params = {}) {
    const body = new URLSearchParams();
    body.set("action", action);

    Object.entries(params).forEach(([key, value]) => {
      body.set(key, value);
    });

    const response = await fetch(backendUrl("MessageServlet"), {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        "X-Requested-With": "XMLHttpRequest"
      },
      body
    });

    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
      throw new Error(data.error || "Message request failed.");
    }

    return data;
  }

  function initMessagesPage() {
    const page = document.querySelector("[data-messages-page]");

    if (!page) {
      return;
    }

    const list = page.querySelector("[data-message-page-list]");
    const status = page.querySelector("[data-message-page-status]");
    const threadTitle = page.querySelector("[data-message-thread-title]");
    const threadMeta = page.querySelector("[data-message-thread-meta]");
    const threadBody = page.querySelector("[data-message-thread-body]");
    const form = page.querySelector("[data-message-form]");
    const textarea = page.querySelector("[data-message-body]");
    const sendButton = form.querySelector("button[type='submit']");
    const params = new URLSearchParams(window.location.search);

    const state = {
      conversations: [],
      activeConversationId: params.get("conversationId") || "",
      accountType: ""
    };

      list.addEventListener("click", (event) => {
      const itemButton = event.target.closest("[data-message-conversation-id]");

      if (!itemButton) {
        return;
      }

      openConversation(itemButton.dataset.messageConversationId);
    });

    form.addEventListener("submit", async (event) => {
      event.preventDefault();

      const body = textarea.value.trim();

      if (!body) {
        showStatus(status, t("messages.emptyMessage"), "error");
        return;
      }

      try {
        await postMessageAction("send", {
          conversationId: state.activeConversationId,
          body
        });
        textarea.value = "";
        showStatus(status, t("messages.sent"), "success");
        window.dispatchEvent(new CustomEvent("studentbridge:messages:refresh"));
        window.dispatchEvent(new CustomEvent("studentbridge:notifications:refresh"));
        await loadConversations();
        await openConversation(state.activeConversationId, false);
      } catch (error) {
        console.error(error);
        showStatus(status, error.message || t("messages.loadError"), "error");
      }
    });

    async function loadConversations() {
      if (!canReachBackend()) {
        showStatus(status, t("messages.loadError"), "error");
        return;
      }

      try {
        const data = await fetchConversations();

        if (!data.loggedIn) {
          window.location.href = frontendUrl("login.html?error=loginRequired&source=messages");
          return;
        }

        state.accountType = data.accountType || "";
        state.conversations = Array.isArray(data.conversations) ? data.conversations : [];
        renderConversationButtons(list, state.conversations, state.activeConversationId);

        if (!state.activeConversationId && state.conversations.length > 0) {
          state.activeConversationId = String(state.conversations[0].id);
        }

        if (state.activeConversationId) {
          await openConversation(state.activeConversationId, false);
        } else {
          renderEmptyThread();
        }
      } catch (error) {
        console.error(error);
        showStatus(status, t("messages.loadError"), "error");
      }
    }

    window.addEventListener("studentbridge:languagechange", () => {
      loadConversations();
    });

    async function openConversation(conversationId, updateUrl = true) {
      if (!conversationId) {
        renderEmptyThread();
        return;
      }

      state.activeConversationId = String(conversationId);

      if (updateUrl) {
        const nextUrl = new URL(window.location.href);
        nextUrl.searchParams.set("conversationId", state.activeConversationId);
        window.history.replaceState({}, "", nextUrl);
      }

      try {
        const response = await fetch(
          backendUrl(`MessageServlet?conversationId=${encodeURIComponent(state.activeConversationId)}`),
          {
            credentials: "include",
            cache: "no-store"
          }
        );
        const data = await response.json().catch(() => ({}));

        if (!response.ok) {
          throw new Error(data.error || t("messages.loadError"));
        }

        renderConversationButtons(list, state.conversations, state.activeConversationId);
        renderThread(data.conversation, Array.isArray(data.messages) ? data.messages : []);
        await postMessageAction("read", { conversationId: state.activeConversationId });
        window.dispatchEvent(new CustomEvent("studentbridge:messages:refresh"));
      } catch (error) {
        console.error(error);
        showStatus(status, error.message || t("messages.loadError"), "error");
      }
    }

    function renderThread(conversation, messages) {
      textarea.disabled = false;
      sendButton.disabled = false;
      translateDisplayText(threadTitle, conversation.jobTitle || t("messages.label"));
      translateMetaParts(threadMeta, [
        { value: conversation.company, translate: true },
        { value: conversation.location, translate: true },
        { value: conversation.otherEmail, translate: false }
      ]);
      threadBody.innerHTML = "";

      if (!messages.length) {
        const empty = document.createElement("p");
        empty.className = "message-empty";
        empty.textContent = t("messages.noPreview");
        threadBody.appendChild(empty);
        return;
      }

      messages.forEach((message) => {
        const bubble = document.createElement("article");
        bubble.className = message.senderAccountType === state.accountType
          ? "message-bubble mine"
          : "message-bubble";

        const text = document.createElement("p");
        translateDisplayText(text, message.body || "");

        const time = document.createElement("span");
        time.textContent = [
          message.senderAccountType,
          formatDate(message.createdAt)
        ].filter(Boolean).join(" · ");

        bubble.append(text, time);
        threadBody.appendChild(bubble);
      });

      threadBody.scrollTop = threadBody.scrollHeight;
    }

    function renderEmptyThread() {
      textarea.disabled = true;
      sendButton.disabled = true;
      threadTitle.textContent = t("messages.chooseThread");
      threadMeta.textContent = "";
      threadBody.innerHTML = "";
      const empty = document.createElement("p");
      empty.className = "message-empty";
      empty.textContent = t("messages.empty");
      threadBody.appendChild(empty);
    }

    loadConversations();
  }

  function initEmployerApplications() {
    const section = document.querySelector("[data-employer-applications]");

    if (!section) {
      return;
    }

    const list = section.querySelector("[data-application-list]");
    const status = section.querySelector("[data-application-status]");

    list.addEventListener("click", async (event) => {
      const button = event.target.closest("[data-start-message-application]");

      if (!button) {
        return;
      }

      button.disabled = true;

      try {
        const data = await postMessageAction("start", {
          applicationId: button.dataset.startMessageApplication
        });
        window.location.href = frontendUrl(
          `messages.html?conversationId=${encodeURIComponent(data.conversationId)}`
        );
      } catch (error) {
        console.error(error);
        showStatus(status, error.message || t("messages.startError"), "error");
        button.disabled = false;
      }
    });

    loadApplications();

    async function loadApplications() {
      if (!canReachBackend()) {
        showStatus(status, t("messages.loadError"), "error");
        return;
      }

      try {
        const response = await fetch(backendUrl("EmployerApplicationsServlet"), {
          credentials: "include",
          cache: "no-store"
        });
        const data = await response.json().catch(() => ({}));

        if (response.status === 401) {
          window.location.href = frontendUrl("login.html?error=loginRequired&source=dashboard");
          return;
        }

        if (!response.ok) {
          throw new Error(data.error || t("messages.loadError"));
        }

        renderApplications(Array.isArray(data.applications) ? data.applications : []);
      } catch (error) {
        console.error(error);
        showStatus(status, error.message || t("messages.loadError"), "error");
      }
    }

    function renderApplications(applications) {
      list.innerHTML = "";

      if (!applications.length) {
        const empty = document.createElement("p");
        empty.className = "message-empty";
        empty.textContent = t("dashboard.noApplications");
        list.appendChild(empty);
        return;
      }

      applications.forEach((application) => {
        const card = document.createElement("article");
        card.className = "application-card";

        const content = document.createElement("div");

        const title = document.createElement("h3");
        title.className = "application-title";
        translateDisplayText(title, application.jobTitle || "Job application");

        const meta = document.createElement("p");
        meta.className = "application-meta";
        translateMetaParts(meta, [
          { value: application.studentEmail, translate: false },
          { value: application.company, translate: true },
          { value: application.location, translate: true },
          { value: `${t("dashboard.appliedAt")}: ${formatDate(application.appliedAt)}`, translate: false }
        ]);

        content.append(title, meta);

        const actions = document.createElement("div");
        actions.className = "application-actions";

        if (Number(application.unreadCount || 0) > 0) {
          const unread = document.createElement("span");
          unread.className = "application-unread";
          unread.textContent = application.unreadCount > 99 ? "99+" : String(application.unreadCount);
          actions.appendChild(unread);
        }

        const cvUrl = safeExternalUrl(application.cvLink);

        if (cvUrl) {
          const cvLink = document.createElement("a");
          cvLink.className = "application-action";
          cvLink.href = cvUrl;
          cvLink.target = "_blank";
          cvLink.rel = "noopener noreferrer";
          cvLink.innerHTML = `<i class="fa-solid fa-file-lines" aria-hidden="true"></i><span>${t("dashboard.viewCv")}</span>`;
          actions.appendChild(cvLink);
        }

        const messageButton = document.createElement("button");
        messageButton.type = "button";
        messageButton.className = "application-action primary";
        messageButton.dataset.startMessageApplication = application.applicationId;
        messageButton.innerHTML = `<i class="fa-solid fa-message" aria-hidden="true"></i><span>${t("dashboard.messageStudent")}</span>`;
        actions.appendChild(messageButton);

        card.append(content, actions);
        list.appendChild(card);
      });
    }

    window.addEventListener("studentbridge:languagechange", () => {
      loadApplications();
    });
  }

  function showStatus(element, text, type) {
    if (!element) {
      return;
    }

    element.textContent = text;
    element.className = `message-status ${type || ""}`.trim();
  }

  function refreshAll() {
    controllers.forEach((controller) => refreshController(controller));
  }

  function init() {
    document.querySelectorAll("[data-message-root]").forEach((root) => {
      setupRoot(root);
    });

    initMessagesPage();
    initEmployerApplications();
  }

  document.addEventListener("click", closeAllPanels);
  window.addEventListener("studentbridge:languagechange", () => {
    controllers.forEach(renderRoot);
  });
  window.addEventListener("studentbridge:messages:refresh", refreshAll);
  window.StudentBridgeMessages = { refresh: refreshAll };

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
