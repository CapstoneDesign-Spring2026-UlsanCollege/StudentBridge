(function () {
  const STORAGE_KEY = "studentBridgeLanguage";
  const DEFAULT_LANGUAGE = "en";
  const config = window.StudentBridgeTranslations || { languages: {}, messages: {} };

  function getSupportedLanguages() {
    return Object.keys(config.languages || {});
  }

  function normalizeLanguage(language) {
    const value = String(language || "").trim().toLowerCase();
    return getSupportedLanguages().includes(value) ? value : DEFAULT_LANGUAGE;
  }

  function getLanguage() {
    return normalizeLanguage(localStorage.getItem(STORAGE_KEY) || DEFAULT_LANGUAGE);
  }

  function translate(key, replacements) {
    const language = getLanguage();
    const englishMessages = config.messages[DEFAULT_LANGUAGE] || {};
    const activeMessages = config.messages[language] || {};
    let value = activeMessages[key] || englishMessages[key] || key;

    Object.entries(replacements || {}).forEach(([name, replacement]) => {
      value = value.replaceAll(`{${name}}`, replacement);
    });

    return value;
  }

  function applyText(element, key) {
    element.textContent = translate(key);
  }

  function applyAttribute(element, attribute, key) {
    element.setAttribute(attribute, translate(key));
  }

  function translatePage(root) {
    const scope = root || document;

    scope.querySelectorAll("[data-i18n]").forEach((element) => {
      applyText(element, element.dataset.i18n);
    });

    scope.querySelectorAll("[data-i18n-placeholder]").forEach((element) => {
      applyAttribute(element, "placeholder", element.dataset.i18nPlaceholder);
    });

    scope.querySelectorAll("[data-i18n-aria-label]").forEach((element) => {
      applyAttribute(element, "aria-label", element.dataset.i18nAriaLabel);
    });

    scope.querySelectorAll("[data-i18n-title]").forEach((element) => {
      applyAttribute(element, "title", element.dataset.i18nTitle);
    });

    document.documentElement.lang = getLanguage();
  }

  function setLanguage(language) {
    const normalizedLanguage = normalizeLanguage(language);
    localStorage.setItem(STORAGE_KEY, normalizedLanguage);
    document.documentElement.lang = normalizedLanguage;
    syncSelectors();
    translatePage();
    window.dispatchEvent(new CustomEvent("studentbridge:languagechange", {
      detail: { language: normalizedLanguage }
    }));
  }

  function syncSelectors() {
    document.querySelectorAll("[data-language-select]").forEach((select) => {
      select.value = getLanguage();
    });
  }

  function createLanguageSwitcher(container) {
    const wrapper = document.createElement("label");
    wrapper.className = "language-switcher";

    const text = document.createElement("span");
    text.setAttribute("data-i18n", "language.label");
    text.textContent = translate("language.label");

    const select = document.createElement("select");
    select.setAttribute("data-language-select", "");
    select.setAttribute("data-i18n-aria-label", "language.label");
    select.setAttribute("aria-label", translate("language.label"));

    Object.entries(config.languages || {}).forEach(([code, label]) => {
      const option = document.createElement("option");
      option.value = code;
      option.textContent = label;
      select.appendChild(option);
    });

    select.value = getLanguage();
    select.addEventListener("change", () => setLanguage(select.value));

    wrapper.append(text, select);
    container.replaceChildren(wrapper);
  }

  function injectStyles() {
    if (document.getElementById("studentbridge-i18n-styles")) {
      return;
    }

    const style = document.createElement("style");
    style.id = "studentbridge-i18n-styles";
    style.textContent = `
      .language-switcher {
        display: inline-flex;
        align-items: center;
        gap: 8px;
        min-height: 40px;
        padding: 6px 10px;
        border: 1px solid rgba(255, 255, 255, 0.16);
        border-radius: 999px;
        color: #e2e8f0;
        background: rgba(255, 255, 255, 0.08);
        font-size: 13px;
        font-weight: 800;
        white-space: nowrap;
      }

      .language-switcher select {
        min-height: 30px;
        border: 0;
        border-radius: 999px;
        color: #0f172a;
        background: #ffffff;
        font: inherit;
        font-weight: 800;
        padding: 4px 8px;
      }

      .auth-language {
        margin-bottom: 18px;
      }

      @media (max-width: 680px) {
        .language-switcher {
          justify-content: center;
          width: 100%;
        }
      }
    `;
    document.head.appendChild(style);
  }

  function init() {
    injectStyles();

    document.querySelectorAll("[data-language-switcher]").forEach((container) => {
      createLanguageSwitcher(container);
    });

    translatePage();
  }

  window.StudentBridgeI18n = {
    getLanguage,
    setLanguage,
    t: translate,
    translatePage,
    isSupported: (language) => getSupportedLanguages().includes(String(language || "").toLowerCase())
  };

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
