document.addEventListener("click", async (e) => {
  console.log("clipboard:", !!navigator.clipboard, "secure:", window.isSecureContext);
  const btn = e.target.closest("[data-js-copy-handle]");
  if (!btn) return;

  const handleEl = document.querySelector("#profile-handle");
  const text = handleEl?.textContent?.trim();
  if (!text) return;

  try {
    await navigator.clipboard.writeText(text);
    btn.blur();
  } catch (err) {
    // fallback
    const range = document.createRange();
    range.selectNodeContents(handleEl);
    const sel = window.getSelection();
    sel.removeAllRanges();
    sel.addRange(range);
  }
});