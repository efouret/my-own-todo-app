<!-- src/routes/+page.svelte -->
<script lang="ts">
    import {onMount} from 'svelte';
    import TodoList from '$lib/components/TodoList.svelte';
    import AddTodo from '$lib/components/AddTodo.svelte';
    import {fetchTodos} from '$lib/stores/todos';

    onMount(() => {
        fetchTodos();
    });

    // Listen for the 'added' event to refresh the todo list if needed
</script>

<main class="section">
    <div class="container">
        <h1 class="title has-text-centered">My Todo App</h1>
        <TodoList/>
        <AddTodo/>
    </div>
</main>

<style>
    main {
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
    }
</style>
