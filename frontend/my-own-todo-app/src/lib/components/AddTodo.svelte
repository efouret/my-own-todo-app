<!-- src/lib/components/AddTodo.svelte -->
<script lang="ts">
    import { createEventDispatcher } from 'svelte'
    import Flatpickr from 'svelte-flatpickr'
    import { addTodo, updateTodo } from '$lib/api/todos'
    import type { Todo } from '$lib/types'
    import 'flatpickr/dist/flatpickr.min.css'
    import '@fortawesome/fontawesome-free/css/all.min.css'

    export let existingTodo: Todo | null = null // For editing existing todos

	const dispatch = createEventDispatcher()

	// Initialize fields based on whether we're adding or editing
	let title = existingTodo?.title || ''
	let dueDate: string | undefined = existingTodo?.dueDate
	let isRecurring = !!existingTodo?.recurringPeriod
	let recurringPeriod = existingTodo?.recurringPeriod?.period || ''
	let recurOnCompletion = existingTodo?.recurringPeriod?.onCompletion || false
	let tags: string[] = existingTodo?.tags || []
	let comment = existingTodo?.description || ''
	let showAdvanced = false

	// Generate unique IDs for accessibility
	let dueDateId = `dueDate-${Math.random().toString(36).substring(2, 11)}`
	let recurringPeriodId = `recurringPeriod-${Math.random().toString(36).substring(2, 11)}`
	let commentId = `comment-${Math.random().toString(36).substring(2, 11)}`

	// Loading and error states
	let isLoading = false
	let errorMessage = ''
	let recurringPeriodError = ''

	const handleSubmit = async () => {
		if (title.trim()) {
			isLoading = true
			errorMessage = ''
			const todoData: Partial<Todo> = {
				title,
				description: comment || undefined,
				dueDate,
				tags: tags.length > 0 ? tags : undefined,
				recurringPeriod: isRecurring
					? {
						period: recurringPeriod,
						onCompletion: recurOnCompletion,
					}
					: undefined,
				comments: comment ? [{ content: comment }] : undefined,
			}

			try {
				if (isRecurring) {
					validateRecurringPeriod()
					if (recurringPeriodError) {
						throw new Error('Invalid recurring period format.')
					}
				}

				if (existingTodo) {
					await updateTodo(existingTodo.id, todoData)
					dispatch('updated')
				} else {
					await addTodo(todoData)
					// Reset fields after adding a new todo
					title = ''
					dueDate = undefined
					isRecurring = false
					recurringPeriod = ''
					recurOnCompletion = false
					tags = []
					comment = ''
				}
				dispatch(existingTodo ? 'updated' : 'added')
			} catch (error) {
				console.error('Error saving todo:', error)
				errorMessage = 'Failed to save todo. Please check your inputs and try again.'
			} finally {
				isLoading = false
			}
		}
	}

	const handleKeyPress = (e: KeyboardEvent) => {
		if (e.key === 'Enter' && !e.shiftKey) {
			e.preventDefault()
			handleSubmit()
		}
	}

	// Validation for ISO 8601 recurring period
	const validateRecurringPeriod = () => {
		const iso8601Pattern = /^P(?=\d|T\d)(\d+Y)?(\d+M)?(\d+D)?(T\d+H)?(T\d+M)?(T\d+S)?$/
		if (isRecurring && recurringPeriod && !iso8601Pattern.test(recurringPeriod)) {
			recurringPeriodError = 'Invalid ISO 8601 duration format.'
		} else {
			recurringPeriodError = ''
		}
	}

	// Watch for changes in recurringPeriod
	$: validateRecurringPeriod()
</script>

<style>
    .icon-button {
        background: none;
        border: none;
        cursor: pointer;
        padding: 0;
        margin-left: 5px;
    }

    .tag {
        margin-right: 5px;
    }

    .options-toggle {
        transition: transform 0.3s ease;
    }

    .options-toggle.expanded {
        transform: rotate(180deg);
    }

    .tags-input {
        display: flex;
        flex-wrap: wrap;
        align-items: center;
        padding: 5px;
        border: 1px solid #dbdbdb;
        border-radius: 4px;
        cursor: text;
    }

    .tags-input input {
        border: none;
        flex: 1;
        padding: 5px;
        min-width: 100px;
    }

    .tags-input input:focus {
        outline: none;
    }

    .tags-container {
        display: flex;
        flex-wrap: wrap;
        gap: 5px;
    }
</style>

<div class="box">
	<!-- Title Field -->
	<div class="field">
		<label for="title" class="label">Title</label>
		<div class="control">
			<input
				id="title"
				class="input is-primary"
				type="text"
				bind:value={title}
				placeholder="Add a new todo"
				on:keypress={handleKeyPress}
				aria-label="Todo title"
			/>
		</div>
	</div>

	{#if showAdvanced}
		<!-- Due Date Field -->
		<div class="field">
			<label for={dueDateId} class="label">Due Date</label>
			<div class="control">
				<Flatpickr
					id={dueDateId}
					bind:value={dueDate}
					options={{ dateFormat: "d M Y" }}
					class="input"
					placeholder="Select due date"
					aria-label="Due date"
				/>
			</div>
		</div>

		<!-- Recurring Checkbox -->
		<div class="field">
			<div class="control">
				<label for="isRecurring" class="checkbox">
					<input id="isRecurring" type="checkbox" bind:checked={isRecurring} />
					Recurring
				</label>
			</div>
		</div>

		{#if isRecurring}
			<!-- Recurring Period Input -->
			<div class="field">
				<label for={recurringPeriodId} class="label">Recurring Period (ISO 8601)</label>
				<div class="control">
					<input
						id={recurringPeriodId}
						class="input {recurringPeriodError ? 'is-danger' : ''}"
						type="text"
						bind:value={recurringPeriod}
						placeholder="e.g., P1D for daily, P6M for six months"
						aria-label="Recurring period in ISO 8601 format"
					/>
				</div>
				{#if recurringPeriodError}
					<p class="help is-danger">{recurringPeriodError}</p>
				{/if}
			</div>

			<!-- Recur on Completion Checkbox -->
			<div class="field">
				<div class="control">
					<label for="recurOnCompletion" class="checkbox">
						<input id="recurOnCompletion" type="checkbox" bind:checked={recurOnCompletion} />
						Recur on completion
					</label>
				</div>
			</div>
		{/if}

		<!-- Comment Field -->
		<div class="field">
			<label for={commentId} class="label">Comment</label>
			<div class="control">
                <textarea
									id={commentId}
									class="textarea"
									bind:value={comment}
									placeholder="Add a comment (optional)"
									aria-label="Add a comment"
								></textarea>
			</div>
		</div>
	{/if}

	<!-- Add/Update and Options Toggle Buttons -->
	<div class="field is-grouped is-grouped-right">
		<div class="control">
			<button
				class="button is-primary"
				on:click={handleSubmit}
				aria-label={existingTodo ? 'Update todo' : 'Add todo'}
				disabled={isLoading}
			>
				{#if isLoading}
                    <span class="icon">
                        <i class="fas fa-spinner fa-spin"></i>
                    </span>
					<span>Processing...</span>
				{:else}
                    <span class="icon">
                        <i class={existingTodo ? 'fas fa-edit' : 'fas fa-plus'}></i>
                    </span>
					<span>{existingTodo ? 'Update' : 'Add'}</span>
				{/if}
			</button>
		</div>
		<div class="control">
			<button
				class="button is-light options-toggle"
				class:expanded={showAdvanced}
				on:click={() => showAdvanced = !showAdvanced}
				title="More options"
				aria-label="Toggle advanced options"
				aria-expanded={showAdvanced}
			>
                <span class="icon">
                    <i class="fas fa-chevron-down"></i>
                </span>
			</button>
		</div>
	</div>

	{#if errorMessage}
		<div class="notification is-danger is-light">
			{errorMessage}
		</div>
	{/if}
</div>
