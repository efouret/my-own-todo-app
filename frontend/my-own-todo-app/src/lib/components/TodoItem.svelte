<!-- src/lib/components/TodoItem.svelte -->
<script lang="ts">
	import { removeTodo, toggleTodo } from '$lib/api/todos'
	import type { Todo } from '$lib/types'

	export let todo: Todo
	export let status: 'late' | 'today' | 'future' = 'future' // Existing prop

	// Modal visibility state
	let isModalActive = false

	// Reference to the "Yes" button to focus when modal opens
	let confirmButton

	// Function to open the confirmation modal
	const openModal = () => {
		isModalActive = true
	}

	// Function to confirm marking as Done
	const confirmToggle = async () => {
		try {
			await toggleTodo(todo.id, { status: 'Done' })
		} catch (error) {
			// Handle error (e.g., show a notification)
			alert('Failed to mark the task as done. Please try again.')
		} finally {
			isModalActive = false
		}
	}

	// Function to cancel the action
	const cancelToggle = () => {
		isModalActive = false
	}

	// Function to handle checkbox change
	const handleToggle = () => {
		if (todo.status !== 'Done') {
			// Open modal for confirmation
			openModal()
		} else {
			// Toggle back to 'Todo' without confirmation
			toggleTodo(todo.id, { status: 'Todo' }).catch((error) => {
				alert('Failed to revert the task status. Please try again.')
			})
		}
	}

	const handleRemove = async () => {
		try {
			await removeTodo(todo.id)
		} catch (error) {
			alert('Failed to delete the task. Please try again.')
		}
	}

	// When the modal becomes active, focus the "Yes" button
	$: if (isModalActive && confirmButton) {
		confirmButton.focus()
	}

	// Handle Escape key to close modal
	const handleKeyDown = (event: KeyboardEvent) => {
		if (event.key === 'Escape') {
			cancelToggle()
		}
	}

	$: {
		if (isModalActive) {
			window.addEventListener('keydown', handleKeyDown)
		} else {
			window.removeEventListener('keydown', handleKeyDown)
		}
	}
</script>

<li class="media todo-item {status}">
	<!-- Media Left: Checkbox -->
	<figure class="media-left">
		<div class="field">
			<input
				class="is-checkradio is-success"
				id="todo-{todo.id}"
				type="checkbox"
				on:change={handleToggle}
				checked={todo.status === 'Done'}
				aria-label={`Mark "${todo.title}" as done`}
			/>
			<label for="todo-{todo.id}"></label>
		</div>
	</figure>

	<!-- Media Content: Todo Details -->
	<div class="media-content">
		<div class="content">
			<p class="{todo.status === 'Done' ? 'has-text-grey-light' : ''}">
				<strong>{todo.title}</strong>
				{#if todo.dueDate}
					<br>
					<small class="has-text-weight-light">Due: {new Date(todo.dueDate).toLocaleDateString()}</small>
				{/if}
			</p>
		</div>
	</div>

	<!-- Media Right: Delete Button -->
	<div class="media-right">
		<button class="delete is-danger" on:click={handleRemove} aria-label="Delete todo"></button>
	</div>

	<!-- Confirmation Modal -->
	{#if isModalActive}
		<div class="modal is-active" role="dialog" aria-modal="true" aria-labelledby="modal-title-{todo.id}">
			<button
				class="modal-background"
				aria-label="Close modal"
				on:click={cancelToggle}
			></button>
			<div class="modal-card">
				<header class="modal-card-head">
					<p class="modal-card-title" id="modal-title-{todo.id}">Confirm Completion</p>
					<button class="delete" aria-label="close" on:click={cancelToggle}></button>
				</header>
				<section class="modal-card-body">
					<p>Are you sure you want to mark "<strong>{todo.title}</strong>" as done?</p>
				</section>
				<footer class="modal-card-foot">
					<button
						class="button is-success mr-2"
						on:click={confirmToggle}
						bind:this={confirmButton}
					>
						Yes
					</button>
					<button class="button" on:click={cancelToggle}>No</button>
				</footer>
			</div>
		</div>
	{/if}
</li>

<style>
    .todo-item {
        display: flex;
        align-items: center;
        padding: 0.75rem 1rem;
        border-radius: 4px;
        margin-bottom: 0.5rem;
        transition: background-color 0.3s, border-left 0.3s;
    }

    /* Status-Based Styling */
    .todo-item.late {
        border-left: 5px solid #e74c3c; /* Red */
        background-color: #fdecea;
    }

    .todo-item.today {
        border-left: 5px solid #f1c40f; /* Yellow */
        background-color: #fff8e1;
    }

    .todo-item.future {
        border-left: 5px solid #2ecc71; /* Green */
        background-color: #eafaf1;
    }

    /* Checkbox Spacing */
    .media-left {
        padding-right: 1rem; /* Adds space between checkbox and content */
    }

    /* Adjust Checkbox Size */
    .is-checkradio {
        margin-top: 0.25rem;
    }

    /* Todo Title Styling */
    .content p {
        margin-bottom: 0;
    }

    .has-text-grey-light {
        text-decoration: line-through;
        color: #7a7a7a;
    }

    /* Responsive Adjustments */
    @media (max-width: 768px) {
        .todo-item {
            flex-direction: column;
            align-items: flex-start;
        }

        .media-right {
            margin-top: 0.5rem;
        }
    }

    /* Modal Customization */
    .modal-card-title {
        font-weight: bold;
    }

    .modal-card-body p {
        font-size: 1rem;
    }

    /* Ensure modal-background button covers the entire screen */
    .modal-background {
        background-color: rgba(10, 10, 10, 0.86); /* Increased opacity for darker overlay */
        border: none;
        padding: 0;
        margin: 0;
        width: 100%;
        height: 100%;
        position: fixed;
        top: 0;
        left: 0;
        cursor: pointer;
        display: block;
        background: rgba(10, 10, 10, 0.86); /* Ensures the background is dark */
    }

    /* Remove default focus outline and add custom focus styles */
    .modal-background:focus {
        outline: 2px solid #3273dc; /* Bulma primary color */
    }

    /* Optional: Enhance Modal Appearance */
    .modal-card-title {
        font-weight: bold;
    }

    .modal-card-body p {
        font-size: 1rem;
    }

    /* Spacing between buttons in modal footer */
    .modal-card-foot .button.is-success {
        margin-right: 0.5rem; /* Adds space between "Yes" and "No" buttons */
    }
</style>
