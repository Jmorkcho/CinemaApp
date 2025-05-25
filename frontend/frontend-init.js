const isDark = localStorage.getItem('darkTheme') === 'true';
if (isDark) {
  document.documentElement.setAttribute('theme', 'dark');
}