import { init, register } from 'svelte-i18n'

// Register your translations (define as many languages as needed)
register('en', () => import('./locales/en.json'))
register('fr', () => import('./locales/fr.json'))

// Initialize the library
init({
	fallbackLocale: 'en',
	initialLocale: 'en', // You could detect this based on the user's settings
})
