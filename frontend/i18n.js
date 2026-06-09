(function () {
  const STORAGE_KEY = "studentBridgeLanguage";
  const DEFAULT_LANGUAGE = "en";
  const config = window.StudentBridgeTranslations || { languages: {}, messages: {} };
  const dynamicCache = new Map();

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

  function hasLocalTranslation(language, key) {
    return Boolean(config.messages[language] && config.messages[language][key]);
  }

  function canUseBackend() {
    return window.StudentBridgePlatform && StudentBridgePlatform.canReachBackend();
  }

  function translationUrl() {
    return StudentBridgePlatform.toBackendUrl("TranslationServlet");
  }

  function dynamicCacheKey(language, text) {
    return `${language}:${text}`;
  }

  async function translateText(text, targetLanguage) {
    const sourceText = String(text || "");
    const language = normalizeLanguage(targetLanguage || getLanguage());

    if (!sourceText.trim() || language === DEFAULT_LANGUAGE || !canUseBackend()) {
      return sourceText;
    }

    const cacheKey = dynamicCacheKey(language, sourceText);

    if (dynamicCache.has(cacheKey)) {
      return dynamicCache.get(cacheKey);
    }

    try {
      const body = new URLSearchParams();
      body.set("target", language);
      body.append("text", sourceText);

      const response = await fetch(translationUrl(), {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
          "X-Requested-With": "XMLHttpRequest"
        },
        body
      });

      if (!response.ok) {
        return sourceText;
      }

      const data = await response.json().catch(() => ({}));
      const translatedText = data.translatedText || sourceText;
      dynamicCache.set(cacheKey, translatedText);
      return translatedText;
    } catch (error) {
      console.error(error);
      return sourceText;
    }
  }

  async function translateBatch(items, targetLanguage) {
    const language = normalizeLanguage(targetLanguage || getLanguage());
    const safeItems = Array.isArray(items) ? items : [];
    const output = {};
    const requestItems = [];

    safeItems.forEach((item, index) => {
      const key = String(item && item.key ? item.key : `item${index}`);
      const text = String(item && item.text ? item.text : "");
      output[key] = text;

      if (!text.trim() || language === DEFAULT_LANGUAGE || !canUseBackend()) {
        return;
      }

      const cacheKey = dynamicCacheKey(language, text);

      if (dynamicCache.has(cacheKey)) {
        output[key] = dynamicCache.get(cacheKey);
        return;
      }

      requestItems.push({ key, text, cacheKey });
    });

    if (!requestItems.length) {
      return output;
    }

    try {
      const body = new URLSearchParams();
      body.set("target", language);

      requestItems.forEach((item) => {
        body.append("key", item.key);
        body.append("text", item.text);
      });

      const response = await fetch(translationUrl(), {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
          "X-Requested-With": "XMLHttpRequest"
        },
        body
      });

      if (!response.ok) {
        return output;
      }

      const data = await response.json().catch(() => ({}));
      const translations = data.translations || {};

      requestItems.forEach((item) => {
        const translatedText = translations[item.key] || item.text;
        dynamicCache.set(item.cacheKey, translatedText);
        output[item.key] = translatedText;
      });
    } catch (error) {
      console.error(error);
    }

    return output;
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
    translateMissingStaticText(scope);
  }

  async function translateMissingStaticText(scope) {
    const language = getLanguage();

    if (language === DEFAULT_LANGUAGE || !canUseBackend()) {
      return;
    }

    const englishMessages = config.messages[DEFAULT_LANGUAGE] || {};
    const items = [];
    const targets = [];

    function addMissing(element, kind, attribute, key) {
      if (!key || hasLocalTranslation(language, key) || !englishMessages[key]) {
        return;
      }

      const itemKey = `${kind}-${items.length}`;
      items.push({ key: itemKey, text: englishMessages[key] });
      targets.push({ itemKey, element, attribute });
    }

    scope.querySelectorAll("[data-i18n]").forEach((element) => {
      addMissing(element, "text", "textContent", element.dataset.i18n);
    });

    scope.querySelectorAll("[data-i18n-placeholder]").forEach((element) => {
      addMissing(element, "placeholder", "placeholder", element.dataset.i18nPlaceholder);
    });

    scope.querySelectorAll("[data-i18n-aria-label]").forEach((element) => {
      addMissing(element, "aria", "aria-label", element.dataset.i18nAriaLabel);
    });

    scope.querySelectorAll("[data-i18n-title]").forEach((element) => {
      addMissing(element, "title", "title", element.dataset.i18nTitle);
    });

    if (!items.length) {
      return;
    }

    const languageAtRequest = language;
    const translated = await translateBatch(items, languageAtRequest);

    if (getLanguage() !== languageAtRequest) {
      return;
    }

    targets.forEach((target) => {
      const translatedValue = translated[target.itemKey];

      if (!target.element.isConnected || !translatedValue) {
        return;
      }

      if (target.attribute === "textContent") {
        target.element.textContent = translatedValue;
      } else {
        target.element.setAttribute(target.attribute, translatedValue);
      }
    });
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

	    const icon = document.createElement("span");
	    icon.className = "language-switcher-icon";
	    icon.setAttribute("aria-hidden", "true");
	    icon.innerHTML = '<i class="fa-solid fa-language"></i>';

	    const text = document.createElement("span");
	    text.className = "sr-only";
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

	    wrapper.append(icon, text, select);
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
	        gap: 7px;
	        min-height: 42px;
	        padding: 6px 8px;
	        border: 1px solid rgba(255, 255, 255, 0.16);
	        border-radius: 999px;
	        color: #e2e8f0;
        background: rgba(255, 255, 255, 0.08);
        font-size: 13px;
        font-weight: 800;
	        white-space: nowrap;
	      }

	      .language-switcher-icon {
	        display: grid;
	        width: 28px;
	        height: 28px;
	        place-items: center;
	        border-radius: 50%;
	        color: #ffffff;
	        background: rgba(255, 255, 255, 0.12);
	        font-size: 14px;
	        flex: 0 0 auto;
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
    translateText,
    translateBatch,
    translatePage,
    isSupported: (language) => getSupportedLanguages().includes(String(language || "").toLowerCase())
  };

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
