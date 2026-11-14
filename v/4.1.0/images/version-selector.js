(function () {
  if (window.__jpDocsVersionSelectorInstalled) {
    return;
  }
  window.__jpDocsVersionSelectorInstalled = true;

  function detectBasePrefix(pathname) {
    var namedRoots = ["/latest/", "/snapshot/"];
    for (var i = 0; i < namedRoots.length; i++) {
      var namedIdx = pathname.indexOf(namedRoots[i]);
      if (namedIdx >= 0) {
        return pathname.slice(0, namedIdx + 1);
      }
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
    if (normalized.startsWith("/snapshot/")) {
      return "snapshot/";
    }
    var releaseMatch = normalized.match(/^\/v\/[^/]+\//);
    if (releaseMatch) {
      return releaseMatch[0].slice(1);
    }
    return "";
  }

  // The Dokka header is dark in both themes, so these use Dokka's dark-header tokens throughout.
  var STYLES =
    ".jp-version-select{" +
    "appearance:none;-webkit-appearance:none;cursor:pointer;" +
    "font:var(--font-text-m);color:var(--color-text-dt);background-color:transparent;" +
    "border:1px solid var(--color-w16);border-radius:var(--size-s1);" +
    "padding:4px 28px 4px 8px;margin:0;" +
    "background-image:url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' " +
    "viewBox='0 0 12 12'%3E%3Cpath d='M2.5 4.5 6 8l3.5-3.5' fill='none' stroke='white' stroke-opacity='.7' " +
    "stroke-width='1.5'/%3E%3C/svg%3E\");" +
    "background-repeat:no-repeat;background-position:right 8px center;}" +
    ".jp-version-select:hover{background-color:var(--color-w10);}" +
    ".jp-version-select:focus-visible{outline:var(--focus-outline);}" +
    ".jp-version-select option{color:var(--color-text-dt);background-color:var(--color-background-nav);}" +
    // Dokka's breakpoint for the narrow header, where the version gets a row of its own with no padding.
    "@media(max-width:899px){.jp-version-select{margin-left:12px;}}";

  function installStyles() {
    var style = document.createElement("style");
    style.textContent = STYLES;
    document.head.appendChild(style);
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

      var select = document.createElement("select");
      select.className = "jp-version-select";
      select.setAttribute("aria-label", "Documentation version");

      data.versions.forEach(function (entry) {
        if (!entry || !entry.label || !entry.path) {
          return;
        }
        var option = document.createElement("option");
        option.textContent = entry.label;
        option.value = (basePrefix + entry.path + relativePath).replace(/\/+/g, "/");
        var showsDefault = docsRoot.length === 0 || docsRoot === "latest/";
        option.selected = showsDefault ? entry.path === data.latest : entry.path === docsRoot;
        select.appendChild(option);
      });

      if (select.options.length < 2) {
        return;
      }

      select.addEventListener("change", function () {
        window.location.assign(select.value);
      });

      // The selected option names this page's version, so it replaces Dokka's plain version label.
      var versionLabel = document.getElementById("library-version");
      var controls = document.querySelector(".navigation-controls");
      if (versionLabel) {
        versionLabel.textContent = "";
        versionLabel.appendChild(select);
      } else if (controls) {
        controls.insertBefore(select, controls.firstChild);
      } else {
        return;
      }
      installStyles();
    })
    .catch(function () {
      // Ignore docs version selector failures to avoid affecting page render.
    });
})();
