(function () {
  if (window.__jpDocsVersionSelectorInstalled) {
    return;
  }
  window.__jpDocsVersionSelectorInstalled = true;

  function detectBasePrefix(pathname) {
    var latestIdx = pathname.indexOf("/latest/");
    if (latestIdx >= 0) {
      return pathname.slice(0, latestIdx + 1);
    }
    var versionIdx = pathname.indexOf("/v/");
    if (versionIdx >= 0) {
      return pathname.slice(0, versionIdx + 1);
    }
    return "/";
  }

  function detectDocsRoot(pathname, basePrefix) {
    var normalized = pathname.slice(basePrefix.length - 1);
    if (normalized.startsWith("/latest/")) {
      return "latest/";
    }
    var releaseMatch = normalized.match(/^\/v\/[^/]+\//);
    if (releaseMatch) {
      return releaseMatch[0].slice(1);
    }
    return "";
  }

  var pathname = window.location.pathname;
  var basePrefix = detectBasePrefix(pathname);
  var docsRoot = detectDocsRoot(pathname, basePrefix);
  var relativePath = pathname;

  if (docsRoot.length > 0) {
    var rootWithSlash = (basePrefix + docsRoot).replace(/\/+/g, "/");
    if (pathname.startsWith(rootWithSlash)) {
      relativePath = pathname.slice(rootWithSlash.length);
    }
  } else {
    relativePath = pathname.slice(basePrefix.length);
  }

  if (relativePath.length === 0) {
    relativePath = "index.html";
  }

  fetch(basePrefix + "versions.json")
    .then(function (response) {
      if (!response.ok) {
        throw new Error("Could not load versions.json");
      }
      return response.json();
    })
    .then(function (data) {
      if (!data || !Array.isArray(data.versions) || data.versions.length === 0) {
        return;
      }

      var container = document.createElement("div");
      container.style.display = "inline-flex";
      container.style.alignItems = "center";
      container.style.gap = "0.4rem";
      container.style.marginLeft = "0.8rem";
      container.style.fontSize = "0.85rem";

      var label = document.createElement("label");
      label.textContent = "Docs:";

      var select = document.createElement("select");
      select.style.fontSize = "0.85rem";

      data.versions.forEach(function (entry) {
        if (!entry || !entry.label || !entry.path) {
          return;
        }
        var option = document.createElement("option");
        option.textContent = entry.label;
        option.value = (basePrefix + entry.path + relativePath).replace(/\/+/g, "/");
        if (docsRoot.length > 0 && docsRoot === entry.path) {
          option.selected = true;
        } else if (docsRoot.length === 0 && entry.path === data.latest) {
          option.selected = true;
        }
        select.appendChild(option);
      });

      if (select.options.length < 2) {
        return;
      }

      select.addEventListener("change", function () {
        window.location.assign(select.value);
      });

      container.appendChild(label);
      container.appendChild(select);

      var target =
        document.querySelector(".navigation .library-name") ||
        document.querySelector(".navigation") ||
        document.querySelector(".breadcrumbs") ||
        document.querySelector("header");

      if (!target) {
        return;
      }
      target.appendChild(container);
    })
    .catch(function () {
      // Ignore docs version selector failures to avoid affecting page render.
    });
})();
