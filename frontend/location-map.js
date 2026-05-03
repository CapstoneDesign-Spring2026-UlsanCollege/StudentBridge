(function () {
  const DEFAULT_CENTER = {
    lat: 35.5384,
    lng: 129.3114
  };

  let kakaoLoaderPromise = null;

  function setStatus(element, message, type = "info") {
    if (!element) {
      return;
    }

    element.textContent = message;
    element.className = `map-status ${type}`;
  }

  function getKakaoKey() {
    return (window.STUDENTBRIDGE_KAKAO_MAP_KEY || "").trim();
  }

  function loadKakaoMaps() {
    if (window.kakao && window.kakao.maps) {
      return Promise.resolve(window.kakao);
    }

    if (kakaoLoaderPromise) {
      return kakaoLoaderPromise;
    }

    const appKey = getKakaoKey();

    if (!appKey) {
      return Promise.reject(new Error("Kakao Maps JavaScript key is missing."));
    }

    kakaoLoaderPromise = new Promise((resolve, reject) => {
      const script = document.createElement("script");
      script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${encodeURIComponent(appKey)}&libraries=services&autoload=false`;
      script.async = true;

      script.onload = () => {
        window.kakao.maps.load(() => resolve(window.kakao));
      };

      script.onerror = () => reject(new Error("Kakao Maps could not be loaded."));

      document.head.appendChild(script);
    });

    return kakaoLoaderPromise;
  }

  function createLatLng(kakao, latitude, longitude) {
    return new kakao.maps.LatLng(Number(latitude), Number(longitude));
  }

  function hasCoordinates(latitude, longitude) {
    return latitude !== "" && longitude !== "" && !Number.isNaN(Number(latitude)) && !Number.isNaN(Number(longitude));
  }

  async function initPicker(options) {
    const mapElement = document.getElementById(options.mapId);
    const addressInput = document.getElementById(options.addressInputId);
    const latitudeInput = document.getElementById(options.latitudeInputId);
    const longitudeInput = document.getElementById(options.longitudeInputId);
    const searchButton = document.getElementById(options.searchButtonId);
    const statusElement = document.getElementById(options.statusId);

    if (!mapElement || !addressInput || !latitudeInput || !longitudeInput || !searchButton) {
      return;
    }

    try {
      const kakao = await loadKakaoMaps();
      const geocoder = new kakao.maps.services.Geocoder();
      const center = hasCoordinates(latitudeInput.value, longitudeInput.value)
        ? createLatLng(kakao, latitudeInput.value, longitudeInput.value)
        : createLatLng(kakao, options.defaultLat || DEFAULT_CENTER.lat, options.defaultLng || DEFAULT_CENTER.lng);

      const map = new kakao.maps.Map(mapElement, {
        center,
        level: options.level || 4
      });

      const marker = new kakao.maps.Marker({
        map,
        position: center
      });

      function updateCoordinates(latlng, message) {
        marker.setPosition(latlng);
        map.setCenter(latlng);
        latitudeInput.value = latlng.getLat().toFixed(7);
        longitudeInput.value = latlng.getLng().toFixed(7);
        setStatus(statusElement, message, "success");
      }

      searchButton.addEventListener("click", () => {
        const address = addressInput.value.trim();

        if (!address) {
          setStatus(statusElement, "Please enter a full address first.", "error");
          addressInput.focus();
          return;
        }

        geocoder.addressSearch(address, (result, status) => {
          if (status !== kakao.maps.services.Status.OK || result.length === 0) {
            setStatus(statusElement, "Address not found. Try a more specific Korean road address.", "error");
            return;
          }

          const coordinates = new kakao.maps.LatLng(result[0].y, result[0].x);
          updateCoordinates(coordinates, "Location selected. Coordinates will be saved with this form.");
        });
      });

      kakao.maps.event.addListener(map, "click", (mouseEvent) => {
        updateCoordinates(mouseEvent.latLng, "Marker moved. Coordinates will be saved with this form.");
      });

      setStatus(statusElement, "Search an address or click the map to choose the exact location.", "info");
    } catch (error) {
      setStatus(statusElement, "Add your Kakao JavaScript key in frontend/map-config.js to enable the map.", "error");
    }
  }

  async function initViewer(options) {
    const mapElement = document.getElementById(options.mapId);
    const statusElement = document.getElementById(options.statusId);

    if (!mapElement) {
      return null;
    }

    try {
      const kakao = await loadKakaoMaps();
      const center = createLatLng(kakao, options.defaultLat || DEFAULT_CENTER.lat, options.defaultLng || DEFAULT_CENTER.lng);
      const map = new kakao.maps.Map(mapElement, {
        center,
        level: options.level || 5
      });
      const marker = new kakao.maps.Marker({
        map,
        position: center
      });
      const infoWindow = new kakao.maps.InfoWindow({
        zIndex: 1
      });

      setStatus(statusElement, "Select a job with saved coordinates to preview it on the map.", "info");

      return {
        showLocation(job) {
          if (!hasCoordinates(job.latitude, job.longitude)) {
            setStatus(statusElement, "This job does not have saved map coordinates yet.", "error");
            return;
          }

          const position = createLatLng(kakao, job.latitude, job.longitude);
          marker.setPosition(position);
          map.setCenter(position);
          infoWindow.setContent(`<div style="padding:8px 10px;font-weight:700;">${escapeHtml(job.title)}</div>`);
          infoWindow.open(map, marker);
          setStatus(statusElement, job.address || `${job.location} · ${job.company}`, "success");
        }
      };
    } catch (error) {
      setStatus(statusElement, "Add your Kakao JavaScript key in frontend/map-config.js to enable job map previews.", "error");
      return null;
    }
  }

  function escapeHtml(value) {
    return String(value || "")
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;")
      .replaceAll("'", "&#039;");
  }

  window.StudentBridgeLocationMap = {
    initPicker,
    initViewer,
    hasCoordinates
  };
})();
