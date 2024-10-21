<!-- src/routes/+page.svelte -->
<script lang="ts">
	import { onMount } from 'svelte'
	import { goto } from '$app/navigation'
	import TodoList from '$lib/components/TodoList.svelte'
	import AddTodo from '$lib/components/AddTodo.svelte'
	import { fetchTodos } from '$lib/stores/todos'
	import { importCsv } from '$lib/api/todos'
	import '../i18n'
	import { init, t } from 'svelte-i18n'
	import { browser } from '$app/environment'

	// State variables
	let isNavbarActive = false
	let isSettingsDropdownActive = false
	let isImportModalActive = false

	onMount(() => {
		fetchTodos()
	})

	// Function to handle Export CSV
	const handleExportCSV = async () => {
		try {
			const response = await fetch('http://your-quarkus-backend.com/api/export-csv', {
				method: 'GET',
				credentials: 'include', // If needed for authentication
			})
			if (!response.ok) throw new Error('Failed to export CSV')
			const blob = await response.blob()
			const url = window.URL.createObjectURL(blob)
			const a = document.createElement('a')
			a.href = url
			a.download = 'todos.csv'
			document.body.appendChild(a)
			a.click()
			a.remove()
			window.URL.revokeObjectURL(url)
		} catch (error) {
			console.error(error)
			// Optionally, display an error message to the user
		}
	}

	// Function to navigate to Settings page
	const navigateToSettings = () => {
		goto('/settings')
	}

	const uploadCsv = async (event: Event) => {
		const form = event.target as HTMLFormElement
		const formData = new FormData(form)
		const csvFile = formData.get('csv') as File
		await importCsv(csvFile)
	}

	if (browser) {
		init({
			fallbackLocale: 'en',
			initialLocale: navigator.language.split('-')[0], // Use 'en' for 'en-US', etc.
		})
	} else {
		init({
			fallbackLocale: 'en',
			initialLocale: 'en',
		})
	}
</script>

<!-- Navbar -->
<nav class="navbar is-primary" aria-label="main navigation">
	<div class="navbar-brand">
		<a class="navbar-item" href="/">
			<strong>{$t("my-own-todo-app-title")}</strong>
		</a>

		<a
			role="button"
			class={`navbar-burger burger ${isNavbarActive ? 'is-active' : ''}`}
			aria-label="menu"
			aria-expanded={isNavbarActive ? 'true' : 'false'}
			data-target="navbarMenu"
			on:click={() => isNavbarActive = !isNavbarActive}
			href="/"
		>
			<span class="icon"><i class="fas fa-gear"></i></span>
		</a>
	</div>

	<div id="navbarMenu" class={`navbar-menu ${isNavbarActive ? 'is-active' : ''}`}>
		<div class="navbar-end">
			<!-- Settings Dropdown -->
			<div class={`navbar-item has-dropdown ${isSettingsDropdownActive ? 'is-active' : ''}`}>
				<a
					href="/"
					class="navbar-link"
					on:click={() => isSettingsDropdownActive = !isSettingsDropdownActive}
				>
					<span class="icon"><i class="fas fa-gear"></i></span>
					<span class="ml-2">Settings</span>
				</a>

				<div class="navbar-dropdown is-right">
					<a href="/" class="navbar-item" on:click={navigateToSettings}>
						<span class="icon"><i class="fas fa-gear"></i></span>
						Settings
					</a>
					<a href="/" class="navbar-item" on:click={() => isImportModalActive = true}>
						<span class="icon"><i class="fas fa-upload"></i></span>
						Import CSV
					</a>
					<a href="/" class="navbar-item" on:click={handleExportCSV}>
						<span class="icon"><i class="fas fa-download"></i></span>
						Export CSV
					</a>
				</div>
			</div>
			<!-- End of Settings Dropdown -->
		</div>
	</div>
</nav>

<!-- Import CSV Modal -->
{#if isImportModalActive}
	<div class="modal is-active">
		<div class="modal-background" on:click={() => isImportModalActive = false}></div>
		<div class="modal-card">
			<header class="modal-card-head">
				<p class="modal-card-title">Import CSV</p>
				<button class="delete" aria-label="close" on:click={() => isImportModalActive = false}></button>
			</header>
			<section class="modal-card-body">
				<!-- File Upload Form -->
				<form
					on:submit|preventDefault={uploadCsv}
				>
					<div class="field">
						<label class="label" for="csv">Select CSV File</label>
						<div class="control">
							<input class="input" type="file" name="csv" accept=".csv" id="csv" required />
						</div>
					</div>
					<div class="field is-grouped is-grouped-right">
						<div class="control">
							<button class="button is-link" type="submit">Upload</button>
						</div>
						<div class="control">
							<button class="button" type="button" on:click={() => isImportModalActive = false}>
								Cancel
							</button>
						</div>
					</div>
				</form>
			</section>
		</div>
	</div>
{/if}

<!-- Main Content -->
<main class="section">
	<div class="container">
		<h1 class="title has-text-centered">My Own Todo App</h1>
		<TodoList />
		<AddTodo />
	</div>
</main>

<style>
    main {
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    /* Optional: Adjust the navbar z-index if necessary */
    .navbar {
        z-index: 1000;
    }

    /* Additional spacing for icons */
    .ml-2 {
        margin-left: 0.5rem;
    }

    .mr-2 {
        margin-right: 0.5rem;
    }

    /* Optional: Style for the icon class */
    .icon {
        width: 1em;
        height: 1em;
    }
</style>
