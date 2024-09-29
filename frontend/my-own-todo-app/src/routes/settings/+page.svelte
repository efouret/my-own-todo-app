<!-- src/routes/settings/+page.svelte -->
<script lang="ts">
	import { goto } from '$app/navigation'
	import { onMount } from 'svelte'
	import { fetchSettings, updateSettings } from '$lib/stores/settings' // Assume you have a settings store

	let settings = {
		// Define your settings fields here
		theme: 'light',
		notifications: true,
	}

	onMount(() => {
		fetchSettings().then((fetchedSettings) => {
			settings = fetchedSettings
		})
	})

	const handleSave = async () => {
		try {
			await updateSettings(settings)
			// Optionally, display a success message
			goto('/') // Navigate back to the main page
		} catch (error) {
			console.error(error)
			// Optionally, display an error message
		}
	}
</script>

<main class="section">
	<div class="container">
		<h1 class="title">Settings</h1>
		<div class="box">
			<form on:submit|preventDefault={handleSave}>
				<div class="field">
					<label class="label">Theme</label>
					<div class="control">
						<div class="select">
							<select bind:value={settings.theme}>
								<option value="light">Light</option>
								<option value="dark">Dark</option>
							</select>
						</div>
					</div>
				</div>

				<div class="field">
					<label class="checkbox">
						<input type="checkbox" bind:checked={settings.notifications} />
						Enable Notifications
					</label>
				</div>

				<!-- Add more settings fields as needed -->

				<div class="field is-grouped is-grouped-right">
					<div class="control">
						<button class="button is-link" type="submit">Save</button>
					</div>
					<div class="control">
						<button
							class="button is-light"
							type="button"
							on:click={() => goto('/')}
						>
							Cancel
						</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</main>

<style>
    main {
        min-height: 100vh;
        padding-top: 2rem;
    }
</style>
