import { cva } from 'class-variance-authority'
import { cn } from '../../lib/utils'

const badgeVariants = cva(
  'inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold transition-colors',
  {
    variants: {
      variant: {
        default:     'border-transparent bg-zinc-900 text-zinc-50',
        secondary:   'border-transparent bg-zinc-100 text-zinc-900',
        outline:     'text-zinc-900',
        destructive: 'border-transparent bg-red-500 text-white',
        success:     'border-transparent bg-emerald-100 text-emerald-800',
        warning:     'border-transparent bg-amber-100 text-amber-800',
        info:        'border-transparent bg-blue-100 text-blue-800',
      },
    },
    defaultVariants: { variant: 'default' },
  }
)

export function Badge({ className, variant, ...props }) {
  return <span className={cn(badgeVariants({ variant }), className)} {...props} />
}
