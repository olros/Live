/* eslint-disable jsx-a11y/anchor-has-content */
/* eslint-disable react/display-name */
import React from 'react';
import Link from 'next/link';

interface IProps extends React.AnchorHTMLAttributes<HTMLAnchorElement> {
  to: string;
  prefetch?: boolean;
  className?: string;
}

export default React.forwardRef(({ to, prefetch, className, ...props }: IProps, ref: any) => {
  return (
    <Link href={to} prefetch={prefetch || false}>
      <a {...props} className={className} ref={ref} />
    </Link>
  );
});
