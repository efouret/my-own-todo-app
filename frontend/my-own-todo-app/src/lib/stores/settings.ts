const fetchSettings = async () => {
	return {
		// Define your settings fields here
		theme: 'light',
		notifications: true,
	}
}

const updateSettings = async (settings: any) => {

}

export { fetchSettings, updateSettings }