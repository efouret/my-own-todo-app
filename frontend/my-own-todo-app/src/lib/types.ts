// src/lib/types.ts
export interface Todo {
    id: string;
    title: string;
    description?: string;
    status: 'Todo' | 'Done';
    dueDate?: string;
    recurringPeriod?: RecurringPeriod;
    comments?: Comment[];
    tags?: string[];
}

export interface RecurringPeriod {
    period: string;
    onCompletion: boolean;
}

export interface Comment {
    content: string;
}
